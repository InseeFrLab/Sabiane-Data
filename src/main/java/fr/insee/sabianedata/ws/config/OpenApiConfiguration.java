package fr.insee.sabianedata.ws.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnExpression("'${spring.profiles.active}'!='prod'")
public class OpenApiConfiguration {

	@Value("${fr.insee.sabianedata.api.scheme}")
	private String apiScheme;
	
	@Value("${fr.insee.sabianedata.api.host}")
	private String apiHost;
	
	@Value("${fr.insee.sabianedata.version}")
	private String projectVersion;

	@Value("${keycloak.auth-server-url:}")
	public String keycloakUrl;

	@Value("${keycloak.realm:}")
	public String realmName;

	public final String SCHEMEKEYCLOAK = "oAuthScheme";

	@Bean
	@ConditionalOnProperty(name = "fr.insee.sabianedata.security", havingValue = "keycloak", matchIfMissing = true)
	public OpenAPI customOpenAPIKeycloak() {
		final OpenAPI openapi = createOpenAPI();
		openapi.components(new Components().addSecuritySchemes(SCHEMEKEYCLOAK, new SecurityScheme()
				.type(SecurityScheme.Type.OAUTH2).in(SecurityScheme.In.HEADER).description("Authentification keycloak")
				.flows(new OAuthFlows().authorizationCode(new OAuthFlow()
						.authorizationUrl(keycloakUrl + "/realms/" + realmName + "/protocol/openid-connect/auth")
						.tokenUrl(keycloakUrl + "/realms/" + realmName + "/protocol/openid-connect/token")
						.refreshUrl(keycloakUrl + "/realms/" + realmName + "/protocol/openid-connect/token")))));
		return openapi;
	}

	@Bean
	@ConditionalOnProperty(name = "fr.insee.sabianedata.security", havingValue = "none")
	public OpenAPI customOpenAPI() {
        return createOpenAPI();
	}

	@ConditionalOnProperty(name = "fr.insee.sabianedata.security", havingValue = "keycloak", matchIfMissing = true)
	@Bean
	public OperationCustomizer ajouterKeycloak() {
		return (operation, handlerMethod) -> {
			if (StringUtils.equalsIgnoreCase("NomDuEndpoint", handlerMethod.getMethod().getName())) {
				return operation;
			}
			return operation.addSecurityItem(new SecurityRequirement().addList(SCHEMEKEYCLOAK));
		};
	}

	public OpenAPI createOpenAPI() {
		OpenAPI openAPI = new OpenAPI()
				.info(
						new Info()
						.title("Build data for Pearl and Queen integration")
						.version(projectVersion)
						.license(new License().name("Apache 2.0").url("http://springdoc.org"))
						);
		final List<Server> servers = new ArrayList<>();
		servers.add(new Server().url(apiScheme+"://"+apiHost));
		openAPI.setServers(servers);
		return openAPI;
	}
}
