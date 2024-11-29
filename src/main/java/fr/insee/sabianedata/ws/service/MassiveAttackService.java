package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

import fr.insee.sabianedata.ws.model.massiveAttack.*;
import fr.insee.sabianedata.ws.model.pearl.*;
import fr.insee.sabianedata.ws.model.queen.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import fr.insee.sabianedata.ws.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MassiveAttackService {

    private final ExternalApiService externalApiService;
    private final ExtractionService extractionService;
    private final TrainingScenarioService trainingScenarioService;
    private final ResourceLoader resourceLoader;

    private File tempFolder;
    private File tempScenariiFolder;

    private final HashMap<String, TrainingScenario> scenarii = new HashMap<>();

    @PostConstruct
    private void init() {
        try {
            setupTempFolders();
            loadScenarios();
            log.debug("Init loading finished: {} scenarii loaded.", scenarii.size());
        } catch (IOException e) {
            log.error("Critical error during initialization of scenarios. Shutting down.", e);
            throw new IllegalStateException("Initialization failed due to IO error", e);
        } catch (Exception e) {
            log.error("Unexpected error during initialization. Shutting down.", e);
            throw new IllegalStateException("Initialization failed due to an unexpected error", e);
        }
    }

    private void setupTempFolders() throws IOException {
        tempFolder = Files.createTempDirectory("folder-").toFile();
        tempScenariiFolder = new File(tempFolder, "scenarii");

        if (!tempScenariiFolder.mkdirs()) {
            log.error("Couldn't create temporary scenarii folder.");
            throw new IOException("Failed to create temporary scenarii folder.");
        }

        File scenariiFolder = resourceLoader.getResource("classpath:scenarii").getFile();
        if (!scenariiFolder.exists()) {
            log.error("Scenarii folder not found in classpath.");
            throw new IOException("Scenarii folder not found in classpath.");
        }

        FileUtils.copyDirectory(scenariiFolder, tempScenariiFolder);
    }

    private void loadScenarios() {
        File[] scenarioFolders = tempScenariiFolder.listFiles();
        if (scenarioFolders == null || scenarioFolders.length == 0) {
            log.error("No scenarii found in the temporary scenarii folder.");
            throw new IllegalStateException("No scenarii found in the temporary folder.");
        }

        Arrays.stream(scenarioFolders)
                .map(file -> trainingScenarioService.getTrainingScenario(tempScenariiFolder, file.getName()))
                .forEach(scenario -> scenarii.put(scenario.getLabel(), scenario));
    }


    @PreDestroy
    private void cleanup() {
        boolean result = FileSystemUtils.deleteRecursively(tempFolder);
        log.debug("Clean-up result : {}", result);
    }

    private void rollBackOnFail(List<String> ids, HttpServletRequest request) {
        log.warn("Roll back : DELETE following campaigns {}", ids);
        ids.forEach(id -> externalApiService.deleteCampaign(request, id));
    }

    public ResponseEntity<String> deleteCampaign(HttpServletRequest request, String id) {
        return externalApiService.deleteCampaign(request, id);
    }

    private TrainingCourse prepareTrainingCourse(String campaignId, String scenario, String campaignLabel,
                                                 String organisationUnitId,
                                                 Long referenceDate, List<String> interviewers,
                                                 ScenarioType type, String scenarLabel) throws Exception {

        // 1 : dossier de traitement 'folder-'
        File currentCampaignFolder = new File(tempScenariiFolder,
                scenario + File.separator + campaignId.toUpperCase());
        File queenFolder = new File(currentCampaignFolder, "queen");
        File pearlFolder = new File(currentCampaignFolder, "pearl");

        // 2 : extract Queen
        File queenFodsInput = new File(queenFolder, "queen_campaign.fods");
        QueenCampaign queenCampaign = extractionService.extractQueenCampaign(queenFodsInput);
        List<QuestionnaireModelDto> questionnaireModels = extractionService
                .extractQuestionnaires(queenFodsInput, queenFolder.toString());
        List<NomenclatureDto> nomenclatures = extractionService
                .extractNomenclatures(queenFodsInput, queenFolder.toString());
        List<QueenSurveyUnit> queenSurveyUnits = extractionService.extractQueenSurveyUnits(queenFodsInput,
                queenFolder.toString());

        // 3 : extract Pearl
        File pearlFodsInput = new File(pearlFolder, "pearl_campaign.fods");

        PearlCampaign pearlCampaign = extractionService
                .extractPearlCampaign(pearlFodsInput);
        List<PearlSurveyUnit> pearlSurveyUnits = extractionService
                .extractPearlSurveyUnits(pearlFodsInput);
        List<Assignement> assignements = extractionService.extractAssignements(pearlFodsInput);

        // 3b : wrap pearl and queen units together for easier id handling
        List<MassiveSurveyUnit> surveyUnits = mergePearlAndQueenSurveyUnits(pearlSurveyUnits, queenSurveyUnits);

        // 3c : wrap pearl and queen campaign together for easier id handling
        MassiveCampaign campaign = new MassiveCampaign(pearlCampaign, queenCampaign);

        // 4 : update campaigns Label and make campaignId uniq => {campaign.id}_{I/M}_{OU}_{date}_{scenarLabel}
        String newCampaignId = String.join("_", pearlCampaign.getCampaign(), type.toString().substring(0, 1),
                organisationUnitId, referenceDate.toString(), scenarLabel);
        campaign.updateCamapignsId(newCampaignId);
        campaign.updateLabel(campaignLabel);

        // 5 : change visibility with user OU only
        updateVisibilities(campaign, referenceDate, organisationUnitId);


        // 6 Queen : make uniq questionnaireId and map oldQuestId to new questModels
        HashMap<String, String> questionnaireIdMapping = new HashMap<>();
        questionnaireModels.forEach(qm -> {
            String initQuestionnaireModelId = qm.getIdQuestionnaireModel();
            String newQuestionnaireModelId = String.join("_",
                    initQuestionnaireModelId,
                    organisationUnitId,
                    referenceDate.toString());
            questionnaireIdMapping.put(initQuestionnaireModelId, newQuestionnaireModelId);
            qm.setIdQuestionnaireModel(newQuestionnaireModelId);
        });

        List<String> newQuestionnaireIds = questionnaireModels.stream()
                .map(QuestionnaireModel::getIdQuestionnaireModel).toList();

        queenCampaign.setQuestionnaireIds(newQuestionnaireIds);


        // 7 : generate pearl survey-units for interviewers
        // big fancy method dispatching survey-unit to trainees
        surveyUnits = generateSurveyUnits(surveyUnits, newCampaignId, referenceDate, organisationUnitId, interviewers,
                assignements, type, questionnaireIdMapping);


        // extract assignements after dispatch
        assignements = extractDistributedAssignements(surveyUnits);


        return new TrainingCourse(surveyUnits, campaign, questionnaireModels,
                nomenclatures, assignements);

    }

    private List<MassiveSurveyUnit> mergePearlAndQueenSurveyUnits(List<PearlSurveyUnit> pearlUnits,
                                                                  List<QueenSurveyUnit> queenUnits) {
        // Create a map of QueenSurveyUnit by their id for quick lookup
        Map<String, QueenSurveyUnit> queenUnitMap = queenUnits.stream()
                .collect(Collectors.toMap(QueenSurveyUnit::getId, queenSu -> queenSu));

        // Map each PearlSurveyUnit to a MassiveSurveyUnit by finding the matching QueenSurveyUnit by id
        return pearlUnits.stream()
                .map(pearlSu -> {
                    QueenSurveyUnit queenSu = queenUnitMap.get(pearlSu.getDisplayName());
                    return new MassiveSurveyUnit(pearlSu.getId(), pearlSu, queenSu); // Create MassiveSurveyUnit
                })
                .toList();

    }

    private List<Assignement> extractDistributedAssignements(
            List<MassiveSurveyUnit> distributedSurveyUnits
    ) {

        return distributedSurveyUnits.stream()
                .map(su -> new Assignement(su.getId(), su.getPearlSurveyUnit().getInterviewerId()))
                .toList();

    }

    /**
     * Take a SurveyUnit `template`,and return a clone of it updated with other params
     *
     * @param surveyUnit         survey-unit to update
     * @param interviewerId      interviewerId to assign
     * @param campaignId         new campaignId
     * @param organisationUnitId new organisationalUnit id
     * @param referenceDate      reference date modifier
     * @param newQuestionnaireId new questionnaire id
     */
    private MassiveSurveyUnit updateSurveyUnit(MassiveSurveyUnit surveyUnit, String interviewerId, String campaignId,
                                               String organisationUnitId, Long referenceDate,
                                               String newQuestionnaireId) {
        // to keep same id in  pearl and queen APIs
        String newId = UUID.randomUUID().toString();

        PearlSurveyUnit pearlSurveyUnit = updatePearlSurveyUnit(surveyUnit.getPearlSurveyUnit(), newId, interviewerId,
                campaignId, organisationUnitId, referenceDate);
        QueenSurveyUnit queenSurveyUnit = updateQueenSurveyUnit(surveyUnit.getQueenSurveyUnit(), newId,
                newQuestionnaireId);
        return new MassiveSurveyUnit(newId, pearlSurveyUnit, queenSurveyUnit);

    }

    private PearlSurveyUnit updatePearlSurveyUnit(
            PearlSurveyUnit initialSurveyUnit, String newId,
            String interviewerId, String campaignId,
            String organisationUnitId, Long referenceDate) {

        PearlSurveyUnit newSu = new PearlSurveyUnit(
                initialSurveyUnit);
        newSu.setInterviewerId(interviewerId);
        newSu.setCampaign(campaignId);
        newSu.setOrganizationUnitId(organisationUnitId);
        newSu.setId(newId);

        // states
        initialSurveyUnit.getStates()
                .stream()
                .map(state -> new SurveyUnitStateDto(state, referenceDate))
                .forEach(newSu.getStates()::add);

        // contactOutcome
        ContactOutcomeDto newContactOutcomeDto = initialSurveyUnit.getContactOutcome() != null
                ? new ContactOutcomeDto(initialSurveyUnit.getContactOutcome(), referenceDate)
                : null;
        newSu.setContactOutcome(newContactOutcomeDto);

        // contactAttempts
        List<ContactAttemptDto> newCAs = Optional.ofNullable(initialSurveyUnit.getContactAttempts())
                .orElse(new ArrayList<>()).stream()
                .map(ca -> new ContactAttemptDto(ca, referenceDate, ca.getMedium()))
                .toList();
        ArrayList<ContactAttemptDto> newContactAttempts = new ArrayList<>(newCAs);
        newSu.setContactAttempts(newContactAttempts);

        return newSu;
    }

    /**
     * Take a Queen survey-unit and return a clone of it updated with other params
     *
     * @param initialSurveyUnit  surveyUnit
     * @param newId              new surveyUnit Id
     * @param newQuestionnaireId new questionnaireId
     * @return the updated clone
     */
    private QueenSurveyUnit updateQueenSurveyUnit(QueenSurveyUnit initialSurveyUnit, String newId,
                                                  String newQuestionnaireId) {
        SurveyUnit newSu = new SurveyUnit(newId, newQuestionnaireId, initialSurveyUnit.getStateDataFile());
        return new QueenSurveyUnit(initialSurveyUnit, newSu);
    }


    /**
     * This is the core method : it takes as input the initial survey-units
     * and generate a copy for each interviewer
     * <p>
     * In Interviewer typed scenario : all Survey-Units are cloned </br>
     * In Manager typed scenario : Survey-Units keep their initial assigned interviewer
     *
     * @param surveyUnits            to use as base
     * @param campaignId             campaign id
     * @param referenceDate          date modification
     * @param organisationUnitId     new organisation-unit id
     * @param trainees               trainees for the training session
     * @param assignments            initial assignments (for MANAGER case)
     * @param type                   MANAGER or INTERVIEWER
     * @param questionnaireIdMapping map linking initial questionnaire ids to generated ids
     * @return dispatched new survey-units
     */
    private List<MassiveSurveyUnit> generateSurveyUnits(
            List<MassiveSurveyUnit> surveyUnits,
            String campaignId,
            Long referenceDate,
            String organisationUnitId,
            List<String> trainees, List<Assignement> assignments,
            ScenarioType type, HashMap<String, String> questionnaireIdMapping) {


        return switch (type) {
            // for each trainee => dispatch each TrainingCourse Survey-unit
            case INTERVIEWER -> trainees.stream()
                    .flatMap(interviewerId -> surveyUnits.stream()
                            .map(surveyUnit -> {
                                        String questId = surveyUnit.getQueenSurveyUnit().getQuestionnaireId();
                                        String newQuestionnaireId = questionnaireIdMapping.get(questId);
                                        return updateSurveyUnit(surveyUnit,
                                                interviewerId,
                                                campaignId,
                                                organisationUnitId,
                                                referenceDate,
                                                newQuestionnaireId);
                                    }
                            )
                    )
                    .toList();

            case MANAGER -> {
                Map<String, String> assignMap = assignments.stream()
                        .collect(Collectors.toMap(Assignement::getSurveyUnitId, Assignement::getInterviewerId));

                yield surveyUnits.stream()
                        .map(surveyUnit -> {
                            String questId = surveyUnit.getQueenSurveyUnit().getQuestionnaireId();
                            String newQuestionnaireId = questionnaireIdMapping.get(questId);

                            return updateSurveyUnit(surveyUnit,
                                    assignMap.get(surveyUnit.getId()),
                                    campaignId,
                                    organisationUnitId,
                                    referenceDate,
                                    newQuestionnaireId);
                        })
                        .toList();
            }
        };
    }


    private void updateVisibilities(MassiveCampaign campaign, Long referenceDate, String organisationUnitId) {
        campaign.getPearlCampaign().getVisibilities().forEach(visibility -> {
            visibility.updateDatesWithReferenceDate(referenceDate);
            visibility.setOrganizationalUnit(organisationUnitId);
        });
    }


    public List<TrainingScenario> getTrainingScenariiTitles() {
        return new ArrayList<>(scenarii.values());
    }


    public ResponseModel generateTrainingScenario(String scenarioId, String campaignLabel,
                                                  String organisationUnitId,
                                                  HttpServletRequest request, Long referenceDate,
                                                  List<String> interviewers) {

        // TODO: use MAP SCENARIOS

        ScenarioType type = trainingScenarioService.getScenarioType(tempScenariiFolder, scenarioId);
        if (type == ScenarioType.INTERVIEWER && !externalApiService.checkInterviewers(interviewers, request)) {
            return new ResponseModel(false, "Error when checking interviewers");
        }
        if (type == ScenarioType.MANAGER && !externalApiService.checkUsers(interviewers, request)) {
            return new ResponseModel(false, "Error when checking users");
        }

        // TODO MAP

        TrainingScenario scenar = trainingScenarioService.getTrainingScenario(tempScenariiFolder, scenarioId);

        if (scenar == null) {
            return new ResponseModel(false, "Error when loading campaigns");
        }

        List<TrainingCourse> trainingCourses = scenar.getCampaigns().stream().map(camp -> {
            try {
                return prepareTrainingCourse(camp.getCampaign(), scenarioId, camp.getCampaignLabel(),
                        organisationUnitId,
                        referenceDate, interviewers, scenar.getType(),
                        campaignLabel);

            } catch (Exception e1) {
                log.error("Couldn't create training course {}", camp.getCampaign(), e1);
                return null;
            }
        }).toList();

        if (trainingCourses.contains(null)) {
            rollBackOnFail(trainingCourses.stream().filter(Objects::nonNull).map(TrainingCourse::getCampaignId)
                    .toList(), request);
            return new ResponseModel(false, "Error when loading campaigns");
        }

        boolean success = trainingCourses.stream()
                .map(tc -> externalApiService.postTrainingCourse(tc, request)).noneMatch(Objects::isNull);

        if (!success) {
            rollBackOnFail(trainingCourses.stream().map(TrainingCourse::getCampaignId)
                    .toList(), request);
            return new ResponseModel(false, "Error when posting campaigns");
        }
        return new ResponseModel(true, "Training scenario generated");
    }


}