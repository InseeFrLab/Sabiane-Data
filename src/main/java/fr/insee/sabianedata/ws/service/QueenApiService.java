package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.model.queen.QueenCampaign;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QueenSurveyUnit;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueenApiService {

    @Value("${fr.insee.sabianedata.queen-api.url}")
    private String queenApiUrl;

    private final RestTemplate restTemplate;

    public ResponseEntity<?> postCampaignToApi(HttpServletRequest request, QueenCampaign queenCampaign) {
        log.info("Creating Campaign {}", queenCampaign.getId());
        final String apiUri = queenApiUrl + "/api/campaigns";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(queenCampaign, httpHeaders), String.class);
    }

    public ResponseEntity<?> postUeToApi(HttpServletRequest request, QueenSurveyUnit queenSurveyUnit,
                                         String idCampaign)  {
        log.info("Create SurveyUnit {}", queenSurveyUnit.getId());
        final String apiUri = queenApiUrl + "/api/campaign/" + idCampaign
                + "/survey-unit";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(queenSurveyUnit, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postNomenclaturesToApi(HttpServletRequest request, NomenclatureDto nomenclatureDto) {
        log.info("Create nomenclature {}", nomenclatureDto.getId());
        final String apiUri = queenApiUrl + "/api/nomenclature";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        log.info("Calling {}", apiUri);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(nomenclatureDto, httpHeaders),
                String.class);
    }

    public ResponseEntity<?> postQuestionnaireModelToApi(HttpServletRequest request,
            QuestionnaireModelDto questionnaireModelDto) {
        log.info("Create Questionnaire {}", questionnaireModelDto.getIdQuestionnaireModel());
        final String apiUri = queenApiUrl + "/api/questionnaire-models";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.POST, new HttpEntity<>(questionnaireModelDto, httpHeaders),
                String.class);
    }

    public HttpHeaders createSimpleHeadersAuth(HttpServletRequest request) {
        String authTokenHeader = request.getHeader("Authorization");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (!StringUtils.isBlank(authTokenHeader))
            httpHeaders.set("Authorization", authTokenHeader);
        return httpHeaders;
    }

    public ResponseEntity<String> deleteCampaign(HttpServletRequest request, String id) {

        // new CampaignDto with parameter id to send to pearl APi
        final String apiUri = queenApiUrl + "/api/campaign/" + id + "?force=true";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.DELETE, new HttpEntity<>(id, httpHeaders), String.class);
    }

    public boolean healthCheck(HttpServletRequest request) {
        final String apiUri = queenApiUrl + "/api/healthcheck";
        HttpHeaders httpHeaders = createSimpleHeadersAuth(request);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(apiUri, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class)
                .getStatusCode().equals(HttpStatus.OK);

    }

}
