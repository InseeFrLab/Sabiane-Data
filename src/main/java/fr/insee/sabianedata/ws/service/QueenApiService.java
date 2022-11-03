package fr.insee.sabianedata.ws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.config.QueenProperties;
import fr.insee.sabianedata.ws.model.queen.CampaignDto;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import fr.insee.sabianedata.ws.model.queen.SurveyUnitDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

@Service
public class QueenApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueenApiService.class);

    @Autowired
    QueenProperties queenProperties;

    @Autowired
    RestTemplate restTemplate;

    public ResponseEntity<?> postCampaignToApi(HttpServletRequest request, CampaignDto campaignDto,
            Plateform plateform) {
        LOGGER.info("Creating Campaign" + campaignDto.getId());
        final String apiUri = queenProperties.getHostFromEnum(plateform) + "/api/campaigns";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(campaignDto, httpHeaders), String.class);
    }

    public ResponseEntity<?> postUeToApi(HttpServletRequest request, SurveyUnitDto surveyUnitDto,
            CampaignDto campaignDto, Plateform plateform) throws JsonProcessingException {
        LOGGER.info("Create SurveyUnit " + surveyUnitDto.getId());
        String idCampaign = campaignDto.getId();
        final String apiUri = queenProperties.getHostFromEnum(plateform) + "/api/campaign/" + idCampaign
                + "/survey-unit";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(surveyUnitDto, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postNomenclaturesToApi(HttpServletRequest request, NomenclatureDto nomenclatureDto,
            Plateform plateform) {
        LOGGER.info("Create nomenclature " + nomenclatureDto.getId());
        final String apiUri = queenProperties.getHostFromEnum(plateform) + "/api/nomenclature";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        LOGGER.info("Calling {}", apiUri);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(nomenclatureDto, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postQuestionnaireModelToApi(HttpServletRequest request,
            QuestionnaireModelDto questionnaireModelDto, Plateform plateform) {
        LOGGER.info("Create Questionnaire " + questionnaireModelDto.getIdQuestionnaireModel());
        final String apiUri = queenProperties.getHostFromEnum(plateform) + "/api/questionnaire-models";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(questionnaireModelDto, httpHeaders),
                String.class);
    }

    public ResponseEntity<String> createFullCampaign(HttpServletRequest request, File campaignZip,
            Plateform plateform) {
        final String apiUri = queenProperties.getHostFromEnum(plateform) + "/api/campaign/context";
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(campaignZip));
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, httpHeaders);
        return restTemplate.postForEntity(apiUri, requestEntity, String.class);
    }

    public HttpHeaders createSimpleHeadersAuth(HttpServletRequest request) {
        String authTokenHeader = request.getHeader("Authorization");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        if (!StringUtils.isBlank(authTokenHeader))
            httpHeaders.set("Authorization", authTokenHeader);
        return httpHeaders;
    }

    public ResponseEntity<String> deleteCampaign(HttpServletRequest request, Plateform plateform, String id) {

        // new CampaignDto with parameter id to send to pearl APi
        final String apiUri = queenProperties.getHostFromEnum(plateform) + "/api/campaign/" + id + "?force=true";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.DELETE, new HttpEntity<>(id, httpHeaders), String.class);
    }

    public boolean healthCheck(HttpServletRequest request, Plateform plateform) {
        final String apiUri = queenProperties.getHostFromEnum(plateform) + "/api/healthcheck";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class)
                .getStatusCode().equals(HttpStatus.OK);

    }

}
