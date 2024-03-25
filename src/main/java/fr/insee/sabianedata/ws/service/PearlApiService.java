package fr.insee.sabianedata.ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.sabianedata.ws.config.PearlProperties;
import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.model.massiveAttack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.massiveAttack.PearlUser;
import fr.insee.sabianedata.ws.model.pearl.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PearlApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PearlApiService.class);

    @Autowired
    PearlProperties pearlProperties;

    @Autowired
    RestTemplate restTemplate;

    public ResponseEntity<?> postCampaignToApi(HttpServletRequest request, CampaignDto campaignDto, Plateform plateform)
            throws JsonProcessingException {
        LOGGER.info("Creating Campaign" + campaignDto.getCampaign());
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/campaign";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(campaignDto, httpHeaders), String.class);
    }

    public ResponseEntity<?> postUesToApi(HttpServletRequest request, List<SurveyUnitDto> surveyUnits,
            Plateform plateform) throws JsonProcessingException {
        LOGGER.info("Create SurveyUnits ");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/survey-units";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(surveyUnits, httpHeaders), String.class);
    }

    public ResponseEntity<?> postInterviewersToApi(HttpServletRequest request, List<InterviewerDto> interviewers,
            Plateform plateform) throws JsonProcessingException {
        LOGGER.info("Create interviewers");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/interviewers";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(interviewers, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postUsersToApi(HttpServletRequest request, List<UserDto> users, String OuId,
            Plateform plateform) throws JsonProcessingException {
        LOGGER.info("Try to create users with id {}", users.stream().map(u -> u.getId()).collect(Collectors.toList()));
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/organization-unit/" + OuId + "/users";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(users, httpHeaders), String.class);
    }

    public ResponseEntity<?> postAssignementsToApi(HttpServletRequest request, List<Assignement> assignements,
            Plateform plateform) {
        LOGGER.info("Create assignements");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/survey-units/interviewers";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(assignements, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postContextToApi(HttpServletRequest request,
            List<OrganisationUnitContextDto> organisationUnits, Plateform plateform) {
        LOGGER.info("Create Context (organisationUnits)");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/organization-units";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(organisationUnits, httpHeaders),
                String.class);
    }

    public HttpHeaders createSimpleHeadersAuth(HttpServletRequest request) {
        String authTokenHeader = request.getHeader("Authorization");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (!StringUtils.isBlank(authTokenHeader))
            httpHeaders.set("Authorization", authTokenHeader);
        return httpHeaders;
    }

    public OrganisationUnitDto getUserOrganizationUnit(HttpServletRequest request, Plateform plateform) {
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/user";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {

            ResponseEntity<PearlUser> userResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
                    new HttpEntity<>(httpHeaders), PearlUser.class);
            if (userResponse.getStatusCode() == HttpStatus.OK) {
                return userResponse.getBody().getOrganisationUnit();
            }
        } catch (Exception e) {
            LOGGER.error("Can't retrieve user organisational-unit");
            LOGGER.error(e.getMessage());
            return null;
        }
        return null;
    }

    public List<Campaign> getCampaigns(HttpServletRequest request, Plateform plateform, boolean admin) {
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/campaigns";
        final String adminApiUri = pearlProperties.getHostFromEnum(plateform) + "/api/admin/campaigns";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        LOGGER.info("Trying to get campaigns list");
        ResponseEntity<Campaign[]> campaignsResponse = restTemplate.exchange(admin ? adminApiUri : apiUri,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders), Campaign[].class);
        if (campaignsResponse.getStatusCode() == HttpStatus.OK) {
            LOGGER.info("API call for campaigns is OK");
            return Arrays.asList(campaignsResponse.getBody());
        } else {
            LOGGER.warn("API call not OK");
        }
        return new ArrayList<>();
    }

    public Optional<CampaignId> getCampaignById(HttpServletRequest request, String id, Plateform plateform) {
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/campaign/" + id;
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        LOGGER.info("Trying to get campaign by ID: {}", id);

        if (id == null || id.isEmpty() || !id.contains("/")) {
            LOGGER.warn("API call for campaign by ID should not be null, empty or containing `/`");
            return Optional.empty();
        }

        try {

            ResponseEntity<CampaignId> campaignResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
                    new HttpEntity<>(httpHeaders), CampaignId.class);

            if (campaignResponse.getStatusCode() == HttpStatus.OK) {

                LOGGER.info("API call for campaign by ID is OK");
                return Optional.ofNullable(campaignResponse.getBody());
            } else {
                LOGGER.warn("API call for campaign by ID not OK");
                return Optional.empty();
            }
        } catch (RestClientException rce) {
            LOGGER.warn("Not found");
        }
        return Optional.empty();

    }

    public ResponseEntity<String> deleteCampaign(HttpServletRequest request, Plateform plateform, String id) {
        LOGGER.info("pearl service : delete");
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/campaign/" + id + "?force=true";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.DELETE, new HttpEntity<>(id, httpHeaders), String.class);
    }

    public boolean healthCheck(HttpServletRequest request, Plateform plateform) {
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/healthcheck";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class)
                .getStatusCode().equals(HttpStatus.OK);

    }

    public List<OrganisationUnitDto> getAllOrganizationUnits(HttpServletRequest request, Plateform plateform) {
        final String apiUri = pearlProperties.getHostFromEnum(plateform) + "/api/organization-units";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        LOGGER.info("Trying to get all organisation units");
        ResponseEntity<OrganisationUnitDto[]> campaignsResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
                new HttpEntity<>(httpHeaders), OrganisationUnitDto[].class);
        if (campaignsResponse.getStatusCode() == HttpStatus.OK) {
            LOGGER.info("API call for all organisation units is OK");
            return Arrays.asList(campaignsResponse.getBody());
        } else {
            LOGGER.warn("API call not OK");
        }
        return new ArrayList<>();
    }

}
