package fr.insee.sabianedata.ws.controller;

import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.model.massiveAttack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.massiveAttack.TrainingScenario;
import fr.insee.sabianedata.ws.model.pearl.Campaign;
import fr.insee.sabianedata.ws.model.pearl.CampaignId;
import fr.insee.sabianedata.ws.service.MassiveAttackService;
import fr.insee.sabianedata.ws.service.PearlApiService;
import fr.insee.sabianedata.ws.service.UtilsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Tag(name = "Massive Attack : Post data to Pearl and Queen APIs")
@RestController
@RequestMapping("/massive-attack/api")
public class MassiveAttackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MassiveAttackController.class);

    @Autowired
    private MassiveAttackService massiveAttackService;

    @Autowired
    private PearlApiService pearlApiService;

    @Autowired
    private UtilsService utilsService;

    @Operation(summary = "Return list of available training courses")
    @GetMapping("training-course-scenario")
    public ResponseEntity<List<TrainingScenario>> getTrainingScenariiTitles() {
        try {
            List<TrainingScenario> scenarii = massiveAttackService.getTrainingScenariiTitles();
            return new ResponseEntity<List<TrainingScenario>>(scenarii, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Can't get training scenarii titles");
            LOGGER.error(e.getMessage());
            return new ResponseEntity<List<TrainingScenario>>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Create a training course")
    @PostMapping(value = "training-course", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseModel> generateTrainingCourse(HttpServletRequest request,
            @RequestParam(value = "campaignId") String campaignId,
            @RequestParam(value = "campaignLabel") String campaignLabel,
            @RequestParam(value = "organisationUnitId") String organisationUnitId,
            @RequestParam(value = "dateReference") Long dateReference,
            @RequestParam(value = "interviewers", defaultValue = "") List<String> interviewers,
            @RequestParam(value = "plateform") Plateform plateform) {
        LOGGER.info("USER : " + utilsService.getRequesterId(request) + " | create scenario " + campaignId + " -> "
                + campaignLabel);
        ResponseModel result = massiveAttackService.generateTrainingScenario(campaignId, campaignLabel,
                organisationUnitId, request,
                dateReference, plateform, interviewers);
        return result.isSuccess() ? ResponseEntity.ok().body(result) : ResponseEntity.badRequest().body(result);
    }

    @Operation(summary = "Return user OrganisationalUnit")
    @GetMapping(value = "user/organisationUnit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrganisationUnitDto> getUserOrganisationalUnit(HttpServletRequest request,
            @RequestParam(value = "plateform") Plateform plateform) {
        LOGGER.info("USER : " + utilsService.getRequesterId(request) + " | get organization unit ");
        OrganisationUnitDto ou = pearlApiService.getUserOrganizationUnit(request, plateform);
        if (ou == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(ou);

    }

    @Operation(summary = "Delete a campaign")
    @DeleteMapping(path = "campaign/{id}")
    public ResponseEntity<String> deleteCampaignById(HttpServletRequest request,
            @PathVariable(value = "id") String campaignId, @RequestParam(value = "plateform") Plateform plateform) {
        LOGGER.warn("USER : " + utilsService.getRequesterId(request) + " | delete campaign " + campaignId);

        if (campaignId == null || campaignId.isEmpty() || !campaignId.contains("/")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return massiveAttackService.deleteCampaign(request, plateform, campaignId);
    }

    @Operation(summary = "Get list of training courses")
    @GetMapping(path = "/training-courses")
    public ResponseEntity<List<Campaign>> getTrainingSessions(HttpServletRequest request,
            @RequestParam(value = "plateform") Plateform plateform,
            @RequestParam(value = "admin", defaultValue = "false") boolean admin) {
        List<Campaign> pearlCampaigns = pearlApiService.getCampaigns(request, plateform, admin);

        LOGGER.info("USER : " + utilsService.getRequesterId(request) + " | get campaigns ");
        return new ResponseEntity<>(pearlCampaigns, HttpStatus.OK);
    }

    @Operation(summary = "Get a training course by ID")
    @GetMapping(path = "/training-courses/{id}")
    public ResponseEntity<CampaignId> getTrainingCourseById(
            HttpServletRequest request,
            @PathVariable("id") String id,
            @RequestParam(value = "plateform") Plateform plateform,
            @RequestParam(value = "admin", defaultValue = "false") boolean admin) {

        if (id == null || id.isEmpty() || !id.contains("/")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<CampaignId> campaign = pearlApiService.getCampaignById(request, id, plateform);

        if (!campaign.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LOGGER.info("USER : {} | get campaign by ID: {}", utilsService.getRequesterId(request), id);
        return new ResponseEntity<>(campaign.get(), HttpStatus.OK);
    }

    @Operation(summary = "Return all OrganisationalUnits")
    @GetMapping(value = "organisation-units", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrganisationUnitDto>> getAllOrganisationalUnits(HttpServletRequest request,
            @RequestParam(value = "plateform") Plateform plateform) {
        LOGGER.info("USER : " + utilsService.getRequesterId(request) + " | get organization unit ");
        List<OrganisationUnitDto> ous = pearlApiService.getAllOrganizationUnits(request, plateform);
        if (ous.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(ous);
    }
}
