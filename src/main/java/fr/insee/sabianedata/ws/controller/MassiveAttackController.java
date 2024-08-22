package fr.insee.sabianedata.ws.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.model.massiveAttack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.massiveAttack.TrainingScenario;
import fr.insee.sabianedata.ws.model.pearl.Campaign;
import fr.insee.sabianedata.ws.service.MassiveAttackService;
import fr.insee.sabianedata.ws.service.PearlApiService;
import fr.insee.sabianedata.ws.service.UtilsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Massive Attack : Post data to Pearl and Queen APIs")
@RestController
@Slf4j
@RequestMapping("/massive-attack/api")
public class MassiveAttackController {

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
            return new ResponseEntity<>(scenarii, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Can't get training scenarii titles");
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        log.info("USER : {} | create scenario {}  -> {}", utilsService.getRequesterId(request), campaignId,
                campaignLabel);
        ResponseModel result = massiveAttackService.generateTrainingScenario(campaignId, campaignLabel,
                organisationUnitId, request,
                dateReference, plateform, interviewers);
        return result.isSuccess() ? ResponseEntity.ok().body(result) : ResponseEntity.badRequest().body(result);
    }

    @Operation(summary = "Return user OrganisationalUnit")
    @GetMapping(value = "user/organisationUnit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrganisationUnitDto> getUserOrganisationalUnit(HttpServletRequest request,
            @RequestParam(value = "plateform") Plateform plateform) {
        log.info("USER : {} | get organization unit ", utilsService.getRequesterId(request));
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
        log.warn("USER : {} | delete campaign {}", utilsService.getRequesterId(request), campaignId);
        return massiveAttackService.deleteCampaign(request, plateform, campaignId);
    }

    @Operation(summary = "Get list of training courses")
    @GetMapping(path = "/training-courses")
    public ResponseEntity<List<Campaign>> getTrainingSessions(HttpServletRequest request,
            @RequestParam(value = "plateform") Plateform plateform,
            @RequestParam(value = "admin", defaultValue = "false") boolean admin) {
        List<Campaign> pearlCampaigns = pearlApiService.getCampaigns(request, plateform, admin);

        log.info("USER : " + utilsService.getRequesterId(request) + " | get campaigns ");
        return new ResponseEntity<>(pearlCampaigns, HttpStatus.OK);
    }

    @Operation(summary = "Return all OrganisationalUnits")
    @GetMapping(value = "organisation-units", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrganisationUnitDto>> getAllOrganisationalUnits(HttpServletRequest request,
            @RequestParam(value = "plateform") Plateform plateform) {
        log.info("USER : " + utilsService.getRequesterId(request) + " | get organization unit ");
        List<OrganisationUnitDto> ous = pearlApiService.getAllOrganizationUnits(request, plateform);
        if (ous.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(ous);
    }
}
