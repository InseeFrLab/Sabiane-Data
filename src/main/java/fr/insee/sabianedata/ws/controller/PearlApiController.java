package fr.insee.sabianedata.ws.controller;

import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.model.pearl.*;
import fr.insee.sabianedata.ws.service.*;
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
import java.util.List;

@Tag(name = "Pearl : Post data to Pearl API")
@RestController
@RequestMapping("/pearl/api")
public class PearlApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PearlApiController.class);

    @Autowired
    private PearlExtractEntities pearlExtractEntities;

    @Autowired
    private PearlApiService pearlApiService;

    @Operation(summary = "Création d'une campagne (unités enquêtées, affectation, dates de collectes)", description = "- **campaign** : le fichier .fods")
    @PostMapping(value = "campaign", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> createFullCampaign(HttpServletRequest request,
            @RequestPart(value = "campaign", required = true) MultipartFile in,
            @RequestParam(value = "plateform") Plateform plateform) throws Exception {

        Path folderTemp = Files.createTempDirectory("folder-");
        File fodsInput = new File(folderTemp.toFile(), in.getOriginalFilename());
        FileUtils.copyInputStreamToFile(in.getInputStream(), fodsInput);

        CampaignDto campaignDto = pearlExtractEntities.getPearlCampaignFromFods(fodsInput);
        List<SurveyUnitDto> surveyUnitDtos = pearlExtractEntities.getPearlSurveyUnitsFromFods(fodsInput);
        List<Assignement> assignements = pearlExtractEntities.getAssignementsFromFods(fodsInput);

        boolean campaignSuccess = false;
        boolean surveyUnitSuccess = false;
        boolean assignementSuccess = false;

        LOGGER.info("Trying to post campaign");
        try {
            pearlApiService.postCampaignToApi(request, campaignDto, plateform);
            campaignSuccess = true;
        } catch (Exception e) {
            LOGGER.error("Error during creation campaign :" + campaignDto.getCampaign());
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Trying to post " + surveyUnitDtos.size() + " surveyUnits");
        try {
            pearlApiService.postUesToApi(request, surveyUnitDtos, plateform);
            surveyUnitSuccess = true;
        } catch (Exception e) {
            LOGGER.error("Error during creation of surveyUnits");
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Trying to post " + assignements.size() + " assignements");
        try {
            pearlApiService.postAssignementsToApi(request, assignements, plateform);
            assignementSuccess = true;
        } catch (Exception e) {
            LOGGER.error("Error during creation of surveyUnits");
            LOGGER.error(e.getMessage());
        }
        boolean success = campaignSuccess && surveyUnitSuccess && assignementSuccess;
        String message = String.format("Campaign : %b, SurveyUnits: %b, Assignements: %b", campaignSuccess,
                surveyUnitSuccess, assignementSuccess);
        ResponseModel responseModel = new ResponseModel(success, message);

        return success ? ResponseEntity.ok().body(responseModel) : ResponseEntity.badRequest().body(responseModel);
    }

    @Operation(summary = "Création des enquêteurs", description = "- **interviewers** : le fichier .fods")
    @PostMapping(value = "interviewers", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> createInterviewers(HttpServletRequest request,
            @RequestPart(value = "interviewers", required = true) MultipartFile in,
            @RequestParam(value = "plateform") Plateform plateform) throws Exception {

        Path folderTemp = Files.createTempDirectory("folder-");
        File fodsInput = new File(folderTemp.toFile(), in.getOriginalFilename());
        FileUtils.copyInputStreamToFile(in.getInputStream(), fodsInput);
        boolean success = false;
        List<InterviewerDto> interviewerDtos = pearlExtractEntities.getPearlInterviewersFromFods(fodsInput);
        LOGGER.info("Trying to post " + interviewerDtos.size() + " interviewers");
        try {
            pearlApiService.postInterviewersToApi(request, interviewerDtos, plateform);
            success = true;
        } catch (Exception e) {
            LOGGER.error("Error during creation of interviewers");
            LOGGER.error(e.getMessage());
        }

        ResponseModel responseModel = new ResponseModel(success, String.format("Create interviewers : %b", success));

        return success ? ResponseEntity.ok().body(responseModel) : ResponseEntity.badRequest().body(responseModel);
    }

    @Operation(summary = "Creation du context (unité organisationnelles)", description = "- **context** : le fichier .fods")
    @PostMapping(value = "context", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> createContext(HttpServletRequest request,
            @RequestPart(value = "context") MultipartFile in, @RequestParam(value = "plateform") Plateform plateform)
            throws Exception {

        Path folderTemp = Files.createTempDirectory("folder-");
        File fodsInput = new File(folderTemp.toFile(), in.getOriginalFilename());
        FileUtils.copyInputStreamToFile(in.getInputStream(), fodsInput);
        boolean success = false;
        List<OrganisationUnitContextDto> organisationUnitDtos = pearlExtractEntities
                .getPearlOrganisationUnitsFromFods(fodsInput);
        try {

            pearlApiService.postContextToApi(request, organisationUnitDtos, plateform);
            success = true;
        } catch (Exception e) {
            LOGGER.error("Error during creation of organisationalUnits");
            LOGGER.error(e.getMessage());
        }

        ResponseModel responseModel = new ResponseModel(success,
                String.format("Create context (organisationalUnits) : %b", success));

        return success ? ResponseEntity.ok().body(responseModel) : ResponseEntity.badRequest().body(responseModel);
    }

    @Operation(summary = "Creation des communes", description = "- **geoLocations** : le fichier .fods")
    @PostMapping(value = "geo-locations", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> createGeoLocations(HttpServletRequest request,
            @RequestPart(value = "geoLocations") MultipartFile in,
            @RequestParam(value = "plateform") Plateform plateform) throws Exception {

        Path folderTemp = Files.createTempDirectory("folder-");
        File fodsInput = new File(folderTemp.toFile(), in.getOriginalFilename());
        FileUtils.copyInputStreamToFile(in.getInputStream(), fodsInput);
        boolean success = false;
        List<GeoLocationDto> geoLocationDtos = pearlExtractEntities.getPearlGeoLocationsFromFods(fodsInput);
        try {
            pearlApiService.postGeoLocationsToApi(request, geoLocationDtos, plateform);
            success = true;
        } catch (Exception e) {
            LOGGER.error("Error during creation of geoLocations");
            LOGGER.error(e.getMessage());
        }

        ResponseModel responseModel = new ResponseModel(success, String.format("Create geoLocations : %b", success));

        return success ? ResponseEntity.ok().body(responseModel) : ResponseEntity.badRequest().body(responseModel);
    }

    @Operation(summary = "Healthcheck, check if api is alive")
    @GetMapping(path = "/healthcheck")
    public ResponseEntity<Object> healthCheck(HttpServletRequest request,
            @RequestParam(value = "plateform") Plateform plateform) {
        LOGGER.debug("HealthCheck");
        boolean pearlIsHealthy = pearlApiService.healthCheck(request, plateform);
        return pearlIsHealthy ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();

    }

}
