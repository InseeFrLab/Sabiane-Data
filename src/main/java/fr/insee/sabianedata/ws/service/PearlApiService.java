package fr.insee.sabianedata.ws.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.insee.sabianedata.ws.model.massiveAttack.OrganisationUnitDto;
import fr.insee.sabianedata.ws.model.massiveAttack.PearlUser;
import fr.insee.sabianedata.ws.model.pearl.Assignement;
import fr.insee.sabianedata.ws.model.pearl.Campaign;
import fr.insee.sabianedata.ws.model.pearl.CampaignDto;
import fr.insee.sabianedata.ws.model.pearl.InterviewerDto;
import fr.insee.sabianedata.ws.model.pearl.SurveyUnitDto;
import fr.insee.sabianedata.ws.model.pearl.UserDto;

@Service
@Slf4j
public class PearlApiService {

    @Value("${fr.insee.sabianedata.pearl-api.url}")
    private String pearlApiUrl;

    @Autowired
    RestTemplate restTemplate;

    public ResponseEntity<?> postCampaignToApi(HttpServletRequest request, CampaignDto campaignDto) {
        log.info("Creating Campaign{}", campaignDto.getCampaign());
        final String apiUri = pearlApiUrl + "/api/campaign";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(campaignDto, httpHeaders), String.class);
    }

    public ResponseEntity<?> postUesToApi(HttpServletRequest request, List<SurveyUnitDto> surveyUnits) {
        log.info("Create SurveyUnits ");
        final String apiUri = pearlApiUrl + "/api/survey-units";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(surveyUnits, httpHeaders), String.class);
    }

    public ResponseEntity<?> postInterviewersToApi(HttpServletRequest request, List<InterviewerDto> interviewers) {
        log.info("Create interviewers");
        final String apiUri = pearlApiUrl + "/api/interviewers";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(interviewers, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postUsersToApi(HttpServletRequest request, List<UserDto> users, String OuId) {
        log.info("Try to create users with id {}", users.stream().map(UserDto::getId).toList());
        final String apiUri = pearlApiUrl + "/api/organization-unit/" + OuId + "/users";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(users, httpHeaders), String.class);
    }

    public ResponseEntity<?> postAssignementsToApi(HttpServletRequest request, List<Assignement> assignements) {
        log.info("Create assignements");
        final String apiUri = pearlApiUrl + "/api/survey-units/interviewers";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(assignements, httpHeaders),
                String.class);
    }

    public HttpHeaders createSimpleHeadersAuth(HttpServletRequest request) {
        String authTokenHeader = request.getHeader("Authorization");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (!StringUtils.isBlank(authTokenHeader)) httpHeaders.set("Authorization", authTokenHeader);
        return httpHeaders;
    }

    public OrganisationUnitDto getUserOrganizationUnit(HttpServletRequest request) {
        final String apiUri = pearlApiUrl + "/api/user";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        try {

            ResponseEntity<PearlUser> userResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
                    new HttpEntity<>(httpHeaders), PearlUser.class);
            if (userResponse.getStatusCode() == HttpStatus.OK) {
                return userResponse.getBody().getOrganisationUnit();
            }
        } catch (Exception e) {
            log.error("Can't retrieve user organisational-unit", e);
            return null;
        }
        return null;
    }

    public List<Campaign> getCampaigns(HttpServletRequest request, boolean admin) {
        final String apiUri = pearlApiUrl + "/api/campaigns";
        final String adminApiUri = pearlApiUrl + "/api/admin/campaigns";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        log.info("Trying to get campaigns list");
        ResponseEntity<Campaign[]> campaignsResponse = restTemplate.exchange(admin ? adminApiUri : apiUri,
                HttpMethod.GET, new HttpEntity<>(httpHeaders), Campaign[].class);
        if (campaignsResponse.getStatusCode() == HttpStatus.OK) {
            log.info("API call for campaigns is OK");
            return Arrays.asList(campaignsResponse.getBody());
        }
        log.warn("Can't get Campaigns list");
        return new ArrayList<>();
    }

    public ResponseEntity<String> deleteCampaign(HttpServletRequest request, String id) {
        log.info("pearl service : delete");
        final String apiUri = pearlApiUrl + "/api/campaign/" + id + "?force=true";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.DELETE, new HttpEntity<>(id, httpHeaders), String.class);
    }

    public boolean healthCheck(HttpServletRequest request) {
        final String apiUri = pearlApiUrl + "/api/healthcheck";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class).getStatusCode().equals(HttpStatus.OK);

    }

    public List<OrganisationUnitDto> getAllOrganizationUnits(HttpServletRequest request) {
        final String apiUri = pearlApiUrl + "/api/organization-units";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        log.info("Trying to get all organisation units");
        ResponseEntity<OrganisationUnitDto[]> campaignsResponse = restTemplate.exchange(apiUri, HttpMethod.GET,
                new HttpEntity<>(httpHeaders), OrganisationUnitDto[].class);
        if (campaignsResponse.getStatusCode() == HttpStatus.OK) {
            log.info("API call for all organisation units is OK");
            return Arrays.asList(campaignsResponse.getBody());
        } else {
            log.warn("Can't get all OUs");
        }
        return new ArrayList<>();
    }

}
