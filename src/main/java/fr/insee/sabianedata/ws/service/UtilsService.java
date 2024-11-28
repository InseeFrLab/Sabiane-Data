package fr.insee.sabianedata.ws.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UtilsService {

    @Value("${fr.insee.sabianedata.security}")
    private String securityMode;

    public String getRequesterId(HttpServletRequest request) {

        if ("keycloak".equals(securityMode)) {
            return request.getUserPrincipal().getName();
        }
        return "GUEST";
    }

}
