package fr.insee.sabianedata.ws.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.model.ReportingModel;
import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.model.queen.*;
import fr.insee.sabianedata.ws.service.QueenExtractEntities;
import fr.insee.sabianedata.ws.service.QueenApiService;
import fr.insee.sabianedata.ws.service.QueenTransformService;
import fr.insee.sabianedata.ws.utils.FileArchiver;
import fr.insee.sabianedata.ws.utils.FilesCleanerUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Tag(name = "Queen : Post data to Queen API")
@RestController
@RequestMapping("/queen/api")
public class QueenApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueenApiController.class);

    @Autowired
    private QueenTransformService queenTransformService;

    @Autowired
    private QueenExtractEntities queenExtractEntities;

    @Autowired
    private QueenApiService queenApiService;

    @Operation(summary = "Insertion de données massives d'une campagne pour Queen", description = "- **campaign** : nom de la campagne à laquelle seront associées les unités enquêtées générées, \n"
            + "- **questionnaireId** : nom du modèle de questionnaire auquel seront associées les unités enquêtées générées, \n"
            + "- **occurrences** : nombre d'unités enquêtées à générer  \n"
            + "- **index** : valeur initiale de l'identifiant  \n\n")
    @PostMapping(value = "integrate-generated-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportingModel> generateAndIntegrateData(HttpServletRequest request,
            @RequestParam(value = "campaign") String campaign,
            @RequestParam(value = "questionnaireId") String questionnaireId,
            @RequestParam(value = "occurrences") int occurrences, @RequestParam(value = "index") int index,
            @RequestParam(value = "plateform") Plateform plateform) throws Exception {

        LOGGER.info("Trying to generate and integrate {} occurrences for campaign {}", occurrences, campaign);

        ReportingModel reporting = new ReportingModel();

        CampaignDto campaignDto = new CampaignDto();
        campaignDto.setId(campaign);

        SurveyUnit su = new SurveyUnit();
        su.setCommentFile("{}");
        su.setPersonalizationFile("{}");
        su.setQuestionnaireId(questionnaireId);
        su.setStateDataFile("{}");
        su.setDataFile("{}");

        IntStream.range(index, occurrences + index).parallel().forEach(q -> {
            SurveyUnitDto suDto = new SurveyUnitDto(su);
            suDto.setId(campaign + q);
            LOGGER.info("su id : {}", suDto.getId());
            try {
                queenApiService.postUeToApi(request, suDto, campaignDto, plateform).getStatusCode();
                reporting.addSuccess(suDto.getId());
                LOGGER.info("Successfully generated and integrated survey unit with id {} for campaign {}",
                        suDto.getId(), campaign);
            } catch (Exception e) {
                reporting.addFailure(suDto.getId());
                LOGGER.error("A problem occurred trying to integrate survey unit with id {}", suDto);
                LOGGER.error(e.getMessage());
            }
        });
        return ResponseEntity.ok().body(reporting);
    }

    @Operation(summary = "Création d'une campagne pour Queen", description = "- **campaign** : le fichier .fods, \n"
            + "- **dataZip** : une archive .zip contenant les fichiers au format json (référerencés dans le fichier .fods)  \n\n"
            + "Un exemple de ces fichiers se trouve dans la partie Exemple du swagger")
    @PostMapping(value = "full-campaign", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> createFullCampaign(HttpServletRequest request,
            @RequestPart(value = "campaign") MultipartFile in, @RequestPart(value = "dataZip") MultipartFile dataZip,
            @RequestParam(value = "plateform") Plateform plateform) throws Exception {

        Path folderTemp = Files.createTempDirectory("folder-");
        File fodsInput = new File(folderTemp.toFile(), in.getOriginalFilename());
        File surveyUnitsDataZip = new File(folderTemp.toFile(), dataZip.getOriginalFilename());
        FileUtils.copyInputStreamToFile(in.getInputStream(), fodsInput);
        FileUtils.copyInputStreamToFile(dataZip.getInputStream(), surveyUnitsDataZip);
        FilesCleanerUtils.unzip(surveyUnitsDataZip);

        CampaignDto campaignDto = queenExtractEntities.getQueenCampaignFromFods(fodsInput);
        List<QuestionnaireModelDto> questionnaireModelDtos = queenExtractEntities
                .getQueenQuestionnaireModelsDtoFromFods(fodsInput, folderTemp.toString());
        List<NomenclatureDto> nomenclatureDtos = queenExtractEntities.getQueenNomenclaturesDtoFromFods(fodsInput,
                folderTemp.toString());
        List<SurveyUnitDto> sus = queenExtractEntities.getQueenSurveyUnitsFromFods(fodsInput, folderTemp.toString());

        long nomenclaturesSuccess;
        long questionnairesSuccess;
        long surveyUnitsSuccess;
        boolean campaignSuccess = false;

        LOGGER.info("Trying to post " + nomenclatureDtos.size() + " nomenclatures");
        nomenclaturesSuccess = nomenclatureDtos.stream().parallel().filter(n -> {
            try {
                queenApiService.postNomenclaturesToApi(request, n, plateform);
                return true;
            } catch (Exception e) {
                LOGGER.error("Error during creation of nomenclature :" + n.getId());
                LOGGER.error(e.getMessage());
                return false;
            }
        }).count();

        LOGGER.info("Trying to post " + questionnaireModelDtos.size() + " questionnaires");
        questionnairesSuccess = questionnaireModelDtos.stream().parallel().filter(q -> {
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
            queenApiService.postCampaignToApi(request, campaignDto, plateform);
            campaignSuccess = true;
        } catch (Exception e) {
            LOGGER.error("Error during creation of campaignDto :" + campaignDto.getId());
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Trying to post " + sus.size() + " survey-units");
        surveyUnitsSuccess = sus.stream().parallel().filter(su -> {
            try {
                queenApiService.postUeToApi(request, su, campaignDto, plateform);
                return true;
            } catch (Exception e) {
                LOGGER.error("Error during creation of surveyUnit :" + su.getId());
                LOGGER.error(e.getMessage());
                return false;
            }
        }).count();

        boolean success = campaignSuccess && nomenclaturesSuccess == nomenclatureDtos.size()
                && questionnairesSuccess == questionnaireModelDtos.size() && surveyUnitsSuccess == sus.size();
        String message = String.format("Nomenclatures: %d/%d, Questionnaires: %d/%d, SurveyUnits: %d/%d, Campaign: %b",
                nomenclaturesSuccess, nomenclatureDtos.size(), questionnairesSuccess, questionnaireModelDtos.size(),
                surveyUnitsSuccess, sus.size(), campaignSuccess);
        ResponseModel responseModel = new ResponseModel(success, message);

        return success ? ResponseEntity.ok().body(responseModel) : ResponseEntity.badRequest().body(responseModel);
    }

    @Operation(summary = "Transform operation described in fods and post ZIP file to Queen Api.", description = "- **campaign** : le fichier .fods, \n"
            + "- **dataZip** : une archive .zip contenant les fichiers au format json (référerencés dans le fichier .fods)  \n\n"
            + "Un exemple de ces fichiers se trouve dans la partie Exemple du swagger")
    @Deprecated
    @PostMapping(value = "full-campaign-2", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> createFullCampaignWithSinglePost(HttpServletRequest request,
            @RequestPart(value = "campaign") MultipartFile in, @RequestPart(value = "dataZip") MultipartFile dataZip,
            @RequestParam(value = "plateform") Plateform plateform) throws Exception {

        Path folderTemp = Files.createTempDirectory("folder-");
        File fodsInput = new File(folderTemp.toFile(), in.getOriginalFilename());
        File surveyUnitsDataZip = new File(folderTemp.toFile(), dataZip.getOriginalFilename());
        FileUtils.copyInputStreamToFile(in.getInputStream(), fodsInput);
        FileUtils.copyInputStreamToFile(dataZip.getInputStream(), surveyUnitsDataZip);
        FilesCleanerUtils.unzip(surveyUnitsDataZip);

        File campaignFile = queenTransformService.getQueenCampaign(fodsInput);
        // Cleaning campaign for integration (delete questionnaireIds)
        CampaignDto campaignDto = queenExtractEntities.getQueenCampaignFromXMLFile(campaignFile);
        campaignDto.setQuestionnaireIds(null);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(campaignFile, campaignDto);

        File questionnaireModels = queenTransformService.getQueenQuestionnaires(fodsInput);
        File nomenclatures = queenTransformService.getQueenNomenclatures(fodsInput);

        File archive = FileArchiver.zipFilesForCreateQueenCampaign(folderTemp.toString(),
                Arrays.asList(campaignFile, questionnaireModels, nomenclatures));
        boolean success = false;
        LOGGER.info("Trying to post full campaign");
        try {
            queenApiService.createFullCampaign(request, archive, plateform);
            success = true;
        } catch (Exception e) {
            LOGGER.error("Error during creation of campaign");
            LOGGER.error(e.getMessage());
        }
        ResponseModel responseModel = new ResponseModel(success, success ? "Ok !" : "Ko !");

        return success ? ResponseEntity.ok().body(responseModel) : ResponseEntity.badRequest().body(responseModel);
    }

    @Operation(summary = "Healthcheck, check if api is alive")
    @GetMapping(path = "/healthcheck")
    public ResponseEntity<Object> healthCheck(HttpServletRequest request,
            @RequestParam(value = "plateform") Plateform plateform) {
        LOGGER.debug("HealthCheck");
        boolean queenIsHealthy = queenApiService.healthCheck(request, plateform);
        return queenIsHealthy ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();

    }
}
