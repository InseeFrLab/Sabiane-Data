package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.client.RestClientException;

import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.model.massiveAttack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.massiveAttack.ScenarioType;
import fr.insee.sabianedata.ws.model.massiveAttack.TrainingCourse;
import fr.insee.sabianedata.ws.model.massiveAttack.TrainingScenario;
import fr.insee.sabianedata.ws.model.pearl.Assignement;
import fr.insee.sabianedata.ws.model.pearl.Campaign;
import fr.insee.sabianedata.ws.model.pearl.ContactAttemptDto;
import fr.insee.sabianedata.ws.model.pearl.ContactOutcomeDto;
import fr.insee.sabianedata.ws.model.pearl.Identification;
import fr.insee.sabianedata.ws.model.pearl.InterviewerDto;
import fr.insee.sabianedata.ws.model.pearl.SurveyUnitStateDto;
import fr.insee.sabianedata.ws.model.pearl.UserDto;
import fr.insee.sabianedata.ws.model.pearl.Visibility;
import fr.insee.sabianedata.ws.model.queen.CampaignDto;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import fr.insee.sabianedata.ws.model.queen.SurveyUnit;
import fr.insee.sabianedata.ws.model.queen.SurveyUnitDto;

@Service
public class MassiveAttackService {

        private static final Logger LOGGER = LoggerFactory.getLogger(MassiveAttackService.class);

        @Autowired
        private QueenExtractEntities queenExtractEntities;

        @Autowired
        private QueenApiService queenApiService;

        @Autowired
        private PearlExtractEntities pearlExtractEntities;

        @Autowired
        private TrainingScenarioService trainingScenarioService;

        @Autowired
        private PearlApiService pearlApiService;

        @Autowired
        ResourceLoader resourceLoader;

        @Value("${fr.insee.sabianedata.security}")
        private String authMode;

        private File tempFolder;
        private File tempScenariiFolder;

        private HashMap<String, TrainingScenario> scenarii = new HashMap<>();

        @PostConstruct
        private void init() {

                try {
                        tempFolder = Files.createTempDirectory("folder-").toFile();
                        tempScenariiFolder = new File(tempFolder, "scenarii");
                        File scenarii = resourceLoader.getResource("classpath:scenarii").getFile();
                        tempScenariiFolder.mkdirs();
                        FileUtils.copyDirectory(scenarii, tempScenariiFolder);
                } catch (IOException e) {
                        e.printStackTrace();
                }

                Stream<File> folders = Arrays.stream(tempScenariiFolder.listFiles());
                List<TrainingScenario> listScenarii = folders
                                .map(f -> trainingScenarioService.getTrainingScenario(tempScenariiFolder, f.getName()))
                                .collect(Collectors.toList());
                listScenarii.forEach(scenar -> scenarii.put(scenar.getLabel(), scenar));
                LOGGER.debug("Init loading finished : {} loaded scenarii", scenarii.size());

        }

        @PreDestroy
        private void cleanup() {
                boolean result = FileSystemUtils.deleteRecursively(tempFolder);
                LOGGER.debug("Clean-up result : " + result);
        }

        private void rollBackOnFail(List<String> ids, HttpServletRequest request, Plateform plateform) {
                LOGGER.warn("Roll back : DELETE following campaigns {}", ids);
                ids.stream().forEach(id -> deleteCampaign(request, plateform, id));
        }

        private TrainingCourse prepareTrainingCourse(String campaign, String scenario, String campaignLabel,
                        String organisationUnitId,
                        HttpServletRequest request, Long referenceDate, Plateform plateform, List<String> interviewers,
                        ScenarioType type, TrainingScenario scenar) throws Exception {

                // 1 : dossier de traitement 'folder-'
                File currentCampaignFolder = new File(tempScenariiFolder,
                                scenario + File.separator + campaign.toUpperCase());
                File queenFolder = new File(currentCampaignFolder, "queen");
                File pearlFolder = new File(currentCampaignFolder, "pearl");

                // 2 : extract Queen
                File queenFodsInput = new File(queenFolder, "queen_campaign.fods");
                CampaignDto queenCampaign = queenExtractEntities.getQueenCampaignFromFods(queenFodsInput);
                List<QuestionnaireModelDto> questionnaireModels = queenExtractEntities
                                .getQueenQuestionnaireModelsDtoFromFods(queenFodsInput, queenFolder.toString());
                List<NomenclatureDto> nomenclatures = queenExtractEntities
                                .getQueenNomenclaturesDtoFromFods(queenFodsInput, queenFolder.toString());
                List<SurveyUnitDto> queenSurveyUnits = queenExtractEntities.getQueenSurveyUnitsFromFods(queenFodsInput,
                                queenFolder.toString());

                // 3 : extract Pearl
                File pearlFodsInput = new File(pearlFolder, "pearl_campaign.fods");

                fr.insee.sabianedata.ws.model.pearl.CampaignDto pearlCampaign = pearlExtractEntities
                                .getPearlCampaignFromFods(pearlFodsInput);
                List<fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto> pearlSurveyUnits = pearlExtractEntities
                                .getPearlSurveyUnitsFromFods(pearlFodsInput);
                List<Assignement> assignements = pearlExtractEntities.getAssignementsFromFods(pearlFodsInput);

                // 4 : make campaignId uniq => {campaign.id}_{OU}{date}
                String newCampaignId = String.join("_", pearlCampaign.getCampaign(), type.toString().substring(0, 1),
                                organisationUnitId, referenceDate.toString());

                pearlCampaign.setCampaign(newCampaignId);
                pearlCampaign.setCampaignLabel(campaignLabel);

                // 5 : change visibility with user OU only and

                ArrayList<Visibility> visibilities = updatingVisibilities(referenceDate, organisationUnitId,
                                pearlCampaign.getVisibilities());

                pearlCampaign.setVisibilities(visibilities);

                // 6 : generate pearl survey-units for interviewers

                List<fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto> distributedPearlSurveyUnits = generatePearlSurveyUnits(
                                campaign, pearlCampaign, referenceDate, organisationUnitId, pearlSurveyUnits,
                                interviewers,
                                assignements, type);

                // 7 Queen : make uniq campaignId and questionnaireId
                String newQueenCampaignId = String.join("_", queenCampaign.getId(), type.toString().substring(0, 1),
                                organisationUnitId, referenceDate.toString());
                queenCampaign.setId(newQueenCampaignId);
                queenCampaign.setLabel(campaignLabel);

                // map oldQuestId to new questModels
                HashMap<String, String> questionnaireIdMapping = new HashMap<>();

                List<QuestionnaireModelDto> newQuestionnaireModels = questionnaireModels.stream().map(qm -> {
                        String newQuestionnaireModelId = String.join("_", qm.getIdQuestionnaireModel(),
                                        organisationUnitId, referenceDate.toString());
                        questionnaireIdMapping.put(qm.getIdQuestionnaireModel(), newQuestionnaireModelId);
                        qm.setIdQuestionnaireModel(newQuestionnaireModelId);
                        qm.setCampaignId(newQueenCampaignId);
                        return qm;
                }).collect(Collectors.toList());

                List<String> newQuestionnaireIds = newQuestionnaireModels.stream()
                                .map(quest -> quest.getIdQuestionnaireModel()).collect(Collectors.toList());

                queenCampaign.setQuestionnaireIds((ArrayList<String>) newQuestionnaireIds);

                // 8 queen : generate queen survey_units

                List<SurveyUnitDto> distributedQueenSurveyUnits = generateQueenSurveyUnits(campaign, referenceDate,
                                queenSurveyUnits, interviewers, assignements, type, questionnaireIdMapping);

                // lambda can't update pearl and queen distributedSU while updating assignements
                // AKA 'local variable in enclosing scope must be final or effectively final'
                // solution is to map meaningfull Id with randomly generated Id

                HashMap<String, String> anonymizedIds = new HashMap<>();

                // update assignements
                assignements = generateDistributedAssignements(distributedPearlSurveyUnits, anonymizedIds);

                // replace meaningfull surveyUnitsIds with random generated ids
                distributedPearlSurveyUnits = distributedPearlSurveyUnits.stream().map(su -> {
                        su.setId(anonymizedIds.get(su.getId()));
                        return su;
                }).collect(Collectors.toList());

                distributedQueenSurveyUnits = distributedQueenSurveyUnits.stream().map(su -> {
                        su.setId(anonymizedIds.get(su.getId()));
                        return su;
                }).collect(Collectors.toList());

                assignements = assignements.stream().map(assignement -> {
                        assignement.setSurveyUnitId(anonymizedIds.get(assignement.getSurveyUnitId()));
                        return assignement;
                }).collect(Collectors.toList());

                TrainingCourse trainingCourse = new TrainingCourse(distributedPearlSurveyUnits,
                                distributedQueenSurveyUnits, pearlCampaign, queenCampaign, newQuestionnaireModels,
                                nomenclatures, assignements);

                return trainingCourse;
        }

        private List<Assignement> generateDistributedAssignements(
                        List<fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto> distributedPearlSurveyUnits,
                        HashMap<String, String> anonymizedIds) {

                return distributedPearlSurveyUnits.stream().map(su -> {
                        String interviewer = su.getId().split("_")[2];
                        String uniqId = UUID.randomUUID().toString().replace("-", "").substring(0, 14);
                        anonymizedIds.put(su.getId(), uniqId);
                        return new Assignement(su.getId(), interviewer);
                }).collect(Collectors.toList());

        }

        private List<fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto> generatePearlSurveyUnits(String campaign,
                        fr.insee.sabianedata.ws.model.pearl.CampaignDto pearlCampaign, Long referenceDate,
                        String organisationUnitId,
                        List<fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto> pearlSurveyUnits,
                        List<String> interviewers, List<Assignement> assignements, ScenarioType type) {

                List<fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto> newSurveyUnits = new ArrayList<>();

                if (type.equals(ScenarioType.INTERVIEWER)) {
                        newSurveyUnits = interviewers.stream().map(in -> pearlSurveyUnits.stream().map(su -> {

                                return updatePearlSurveyUnit(su, in, pearlCampaign, campaign, organisationUnitId,
                                                referenceDate);
                        }).collect(Collectors.toList())

                        ).flatMap(Collection::stream).collect(Collectors.toList());

                }
                if (type.equals(ScenarioType.MANAGER)) {
                        Map<String, String> assignMap = assignements.stream().collect(
                                        Collectors.toMap(Assignement::getSurveyUnitId, Assignement::getInterviewerId));
                        newSurveyUnits = pearlSurveyUnits.stream()
                                        .map(su -> updatePearlSurveyUnit(su, assignMap.get(su.getId()), pearlCampaign,
                                                        campaign, organisationUnitId, referenceDate))
                                        .collect(Collectors.toList());

                }

                return newSurveyUnits;
        }

        private fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto updatePearlSurveyUnit(
                        fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto initialSurveyUnit, String interviewer,
                        fr.insee.sabianedata.ws.model.pearl.CampaignDto pearlCampaign, String campaign,
                        String organisationUnitId, Long referenceDate) {

                fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto newSu = new fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto(
                                initialSurveyUnit);
                newSu.setCampaign(pearlCampaign.getCampaign());
                newSu.setOrganizationUnitId(organisationUnitId);
                newSu.setId(String.join("_", initialSurveyUnit.getId(), campaign, interviewer,
                                referenceDate.toString()));

                // states
                List<SurveyUnitStateDto> statesList = Optional.ofNullable(initialSurveyUnit.getStates())
                                .orElse(new ArrayList<>()).stream()
                                .map(state -> new SurveyUnitStateDto(state, referenceDate))
                                .collect(Collectors.toList());
                ArrayList<SurveyUnitStateDto> newStates = new ArrayList<>(statesList);
                newSu.setStates(newStates);

                // contactOutcome
                ContactOutcomeDto newContactOutcomeDto = initialSurveyUnit.getContactOutcome() != null
                                ? new ContactOutcomeDto(initialSurveyUnit.getContactOutcome(), referenceDate)
                                : null;
                newSu.setContactOutcome(newContactOutcomeDto);

                // contactAttempts
                List<ContactAttemptDto> newCAs = Optional.ofNullable(initialSurveyUnit.getContactAttempts())
                                .orElse(new ArrayList<>()).stream()
                                .map(ca -> new ContactAttemptDto(ca, referenceDate, ca.getMedium()))
                                .collect(Collectors.toList());
                ArrayList<ContactAttemptDto> newContactAttempts = new ArrayList<>(newCAs);
                newSu.setContactAttempts(newContactAttempts);

                return newSu;
        }

        private SurveyUnitDto updateQueenSurveyUnit(SurveyUnitDto initialSurveyUnit, String interviewer,
                        String campaign, String newQuestionnaireId, Long referenceDate) {

                String newId = String.join("_", initialSurveyUnit.getId(), campaign, interviewer,
                                referenceDate.toString());
                SurveyUnit newSu = new SurveyUnit(newId, newQuestionnaireId, initialSurveyUnit.getStateData());
                SurveyUnitDto newSuDto = new SurveyUnitDto(initialSurveyUnit, newSu);
                return newSuDto;

        }

        private List<SurveyUnitDto> generateQueenSurveyUnits(String campaign, Long referenceDate,
                        List<SurveyUnitDto> queenSurveyUnits, List<String> interviewers, List<Assignement> assignements,
                        ScenarioType type, HashMap<String, String> questionnaireIdMapping) {
                List<SurveyUnitDto> newSurveyUnits = new ArrayList<>();
                if (type.equals(ScenarioType.INTERVIEWER)) {
                        newSurveyUnits = interviewers.stream().map(in -> queenSurveyUnits.stream().map(sudto -> {
                                String newQuestionnaireId = questionnaireIdMapping.get(sudto.getQuestionnaireId());
                                return updateQueenSurveyUnit(sudto, in, campaign, newQuestionnaireId, referenceDate);

                        }).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
                }
                if (type.equals(ScenarioType.MANAGER)) {
                        Map<String, String> assignMap = assignements.stream().collect(
                                        Collectors.toMap(Assignement::getSurveyUnitId, Assignement::getInterviewerId));
                        newSurveyUnits = queenSurveyUnits.stream().map(su -> {
                                String newQuestionnaireId = questionnaireIdMapping.get(su.getQuestionnaireId());
                                return updateQueenSurveyUnit(su, assignMap.get(su.getId()), campaign,
                                                newQuestionnaireId, referenceDate);
                        }).collect(Collectors.toList());

                }
                return newSurveyUnits;
        }

        private ArrayList<Visibility> updatingVisibilities(Long referenceDate, String organisationUnitId,
                        List<Visibility> previousVisibilities) {

                List<Visibility> newVisibilities = previousVisibilities.stream()
                                .map(v -> new Visibility(v, referenceDate)).map(v -> {
                                        v.setOrganizationalUnit(organisationUnitId);
                                        return v;
                                }).collect(Collectors.toList());

                return new ArrayList<Visibility>(newVisibilities);

        }

        public List<TrainingScenario> getTrainingScenariiTitles() throws Exception {
                return new ArrayList<>(scenarii.values());
        }

        public TrainingCourse postTrainingCourse(TrainingCourse tc, HttpServletRequest request, Long referenceDate,
                        Plateform plateform, List<String> interviewers) {

                boolean pearlCampaignSuccess = false;
                boolean pearlSurveyUnitSuccess = false;
                boolean assignementSuccess = false;

                LOGGER.info("Trying to post pearl campaign");
                try {
                        pearlApiService.postCampaignToApi(request, tc.getPearlCampaign(), plateform);
                        pearlCampaignSuccess = true;
                } catch (Exception e) {
                        LOGGER.error("Error during creation campaign :" + tc.getPearlCampaign().getCampaign());
                        LOGGER.error(e.getMessage());
                }
                LOGGER.info("Trying to post " + tc.getPearlSurveyUnits().size() + " pearl surveyUnits");
                tc.getPearlSurveyUnits().stream().parallel().forEach(su -> {
                        Identification ident = su.getIdentification();
                        LOGGER.info(su.getId() + " - " +
                                        ident == null ? "ident is null"
                                                        : (ident.getIdentification() + ident.getAccess()
                                                                        + ident.getSituation()));
                });
                try {
                        pearlApiService.postUesToApi(request, tc.getPearlSurveyUnits(), plateform);
                        pearlSurveyUnitSuccess = true;
                } catch (Exception e) {
                        LOGGER.error("Error during creation of surveyUnits");
                        LOGGER.error(e.getMessage());
                }
                LOGGER.info("Trying to post " + tc.getAssignements().size() + " assignements");
                try {
                        pearlApiService.postAssignementsToApi(request, tc.getAssignements(), plateform);
                        assignementSuccess = true;
                } catch (Exception e) {
                        LOGGER.error("Error during creation of assignements");
                        LOGGER.error(e.getMessage());
                }
                boolean pearlSuccess = pearlCampaignSuccess && pearlSurveyUnitSuccess && assignementSuccess;
                String pearlMessage = String.format("Campaign : %b, SurveyUnits: %b, Assignements: %b",
                                pearlCampaignSuccess, pearlSurveyUnitSuccess, assignementSuccess);
                LOGGER.info(pearlMessage);

                // POST queen entities
                long nomenclaturesSuccess;
                long questionnairesSuccess;
                long queenSurveyUnitsSuccess;
                boolean queenCampaignSuccess = false;

                LOGGER.info("Trying to post " + tc.getNomenclatures().size() + " nomenclatures");
                nomenclaturesSuccess = tc.getNomenclatures().stream().parallel().filter(n -> {
                        try {
                                queenApiService.postNomenclaturesToApi(request, n, plateform);
                                return true;
                        } catch (Exception e) {
                                LOGGER.error("Error during creation of nomenclature :" + n.getId());
                                LOGGER.error(e.getMessage());
                                return false;
                        }
                }).count();

                LOGGER.info("Trying to post " + tc.getQuestionnaireModels().size() + " questionnaires");
                questionnairesSuccess = tc.getQuestionnaireModels().stream().parallel().filter(q -> {
                        try {
                                queenApiService.postQuestionnaireModelToApi(request, q, plateform);
                                return true;
                        } catch (Exception e) {
                                LOGGER.error("Error during creation of questionnaire :" + q.getIdQuestionnaireModel());
                                LOGGER.error(e.getMessage());
                                return false;
                        }
                }).count();

                LOGGER.info("Trying to post campaign");
                try {
                        queenApiService.postCampaignToApi(request, tc.getQueenCampaign(), plateform);
                        queenCampaignSuccess = true;
                } catch (Exception e) {
                        LOGGER.error("Error during creation of campaignDto :" + tc.getQueenCampaign().getId());
                        LOGGER.error(e.getMessage());
                }
                LOGGER.info("Trying to post " + tc.getQueenSurveyUnits().size() + " queen survey-units");
                queenSurveyUnitsSuccess = tc.getQueenSurveyUnits().stream().parallel().filter(su -> {
                        try {
                                queenApiService.postUeToApi(request, su, tc.getQueenCampaign(), plateform);
                                return true;
                        } catch (Exception e) {
                                LOGGER.error("Error during creation of surveyUnit :" + su.getId());
                                LOGGER.error(e.getMessage());
                                return false;
                        }
                }).count();

                boolean queenSuccess = queenCampaignSuccess && nomenclaturesSuccess == tc.getNomenclatures().size()
                                && questionnairesSuccess == tc.getQuestionnaireModels().size()
                                && queenSurveyUnitsSuccess == tc.getQueenSurveyUnits().size();
                String queenMessage = String.format(
                                "Nomenclatures: %d/%d, Questionnaires: %d/%d, SurveyUnits: %d/%d, Campaign: %b",
                                nomenclaturesSuccess, tc.getNomenclatures().size(), questionnairesSuccess,
                                tc.getQuestionnaireModels().size(), queenSurveyUnitsSuccess,
                                tc.getQueenSurveyUnits().size(), queenCampaignSuccess);

                LOGGER.info(queenMessage);

                return pearlSuccess && queenSuccess ? tc : null;
        }

        public ResponseModel generateTrainingScenario(String scenarioId, String campaignLabel,
                        String organisationUnitId,
                        HttpServletRequest request, Long referenceDate, Plateform plateform,
                        List<String> interviewers) {

                ScenarioType type = trainingScenarioService.getScenarioType(tempScenariiFolder, scenarioId);
                if (type == ScenarioType.INTERVIEWER && !checkInterviewers(interviewers, request, plateform)) {
                        return new ResponseModel(false, "Error when checking interviewers");
                }
                if (type == ScenarioType.MANAGER && !checkInterviewers(interviewers, request, plateform)) {
                        return new ResponseModel(false, "Error when checking users");
                }

                TrainingScenario scenar = trainingScenarioService.getTrainingScenario(tempScenariiFolder, scenarioId);

                List<TrainingCourse> trainingCourses = scenar.getCampaigns().stream().map(camp -> {
                        try {
                                return prepareTrainingCourse(camp.getCampaign(), scenarioId, camp.getCampaignLabel(),
                                                organisationUnitId,
                                                request, referenceDate, plateform, interviewers, scenar.getType(),
                                                scenar);
                        } catch (Exception e1) {
                                LOGGER.error("coudn't create training course " + camp.getCampaign(), e1);
                                e1.printStackTrace();
                                return null;
                        }
                }).collect(Collectors.toList());

                if (trainingCourses.contains(null)) {
                        rollBackOnFail(trainingCourses.stream().filter(tc -> tc != null).map(tc -> tc.getCampaignId())
                                        .collect(Collectors.toList()), request, plateform);
                        return new ResponseModel(false, "Error when loading campaigns");
                }

                boolean success = trainingCourses.stream()
                                .map(tc -> postTrainingCourse(tc, request, referenceDate, plateform, interviewers))
                                .filter(tc -> tc == null).collect(Collectors.toList()).size() == 0;

                if (!success) {
                        rollBackOnFail(trainingCourses.stream().map(tc -> tc.getCampaignId())
                                        .collect(Collectors.toList()), request, plateform);
                        return new ResponseModel(false, "Error when posting campaigns");
                }
                return new ResponseModel(true, "Training scenario generated");
        }

        public boolean checkInterviewers(List<String> interviewers, HttpServletRequest request, Plateform plateform) {
                InterviewerDto validInterviewer = new InterviewerDto();
                ArrayList<InterviewerDto> interviewerList = new ArrayList<>();
                interviewerList.add(validInterviewer);
                validInterviewer.setFirstName("FirstName");
                validInterviewer.setLastName("LastName");
                validInterviewer.setEmail("firstname.lastname@valid.net");
                validInterviewer.setPhoneNumber("+33000000000");
                return interviewers.stream().map(inter -> {
                        validInterviewer.setId(inter);
                        try {
                                ResponseEntity<?> postResponse = pearlApiService.postInterviewersToApi(request,
                                                interviewerList, plateform);
                                LOGGER.info("Interviewer " + inter + " created.");
                                return postResponse;
                        } catch (JsonProcessingException e) {
                                LOGGER.warn("Error when creating interviewer " + inter);
                                LOGGER.error(e.getMessage());
                                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        } catch (RestClientException e) {
                                LOGGER.info("Interviewer " + inter + " already present.");
                                LOGGER.debug(e.getMessage());

                                return new ResponseEntity<>(HttpStatus.OK);
                        }

                }).filter(response -> !response.getStatusCode().is2xxSuccessful()).collect(Collectors.toList())
                                .size() == 0;

        }

        public boolean checkUsers(List<String> users, HttpServletRequest request, Plateform plateform) {

                OrganisationUnitDto ou = pearlApiService.getUserOrganizationUnit(request, plateform);
                if (ou == null) {
                        LOGGER.warn("Can't get organizationUnit of caller");
                        return false;
                }

                UserDto validUser = new UserDto();
                ArrayList<UserDto> userList = new ArrayList<>();
                userList.add(validUser);
                validUser.setFirstName("FirstName");
                validUser.setLastName("LastName");
                return users.stream().map(user -> {
                        validUser.setId(user);
                        try {
                                ResponseEntity<?> postResponse = pearlApiService.postUsersToApi(request, userList,
                                                ou.getId(), plateform);
                                LOGGER.info("User " + user + " created.");
                                return postResponse;
                        } catch (JsonProcessingException e) {
                                LOGGER.warn("Error when creating user " + user);
                                LOGGER.error(e.getMessage());
                                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                        } catch (RestClientException e) {
                                LOGGER.info("User " + user + " already present.");
                                LOGGER.debug(e.getMessage());

                                return new ResponseEntity<>(HttpStatus.OK);
                        }

                }).filter(response -> !response.getStatusCode().is2xxSuccessful()).collect(Collectors.toList())
                                .size() == 0;

        }

        public ResponseEntity<String> deleteCampaign(HttpServletRequest request, Plateform plateform, String id) {
                List<Campaign> pearlCampaigns = pearlApiService.getCampaigns(request, plateform, true);
                if (pearlCampaigns.stream().filter(camp -> camp.getId().equals(id)).count() == 0) {
                        LOGGER.error("DELETE campaign with id {} resulting in 404 because it does not exists", id);
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                ResponseEntity<String> pearlResponse = pearlApiService.deleteCampaign(request, plateform, id);
                ResponseEntity<String> queenResponse = queenApiService.deleteCampaign(request, plateform, id);
                LOGGER.info("DELETE campaign with id {} : pearl={} / queen={}", id,
                                pearlResponse.getStatusCode().toString(), queenResponse.getStatusCode().toString());
                return ResponseEntity.ok().build();

        }

}