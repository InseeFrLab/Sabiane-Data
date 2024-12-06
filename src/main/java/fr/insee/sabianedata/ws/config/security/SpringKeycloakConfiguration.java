package fr.insee.sabianedata.ws.config.security;


import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "fr.insee.sabianedata.security", havingValue = "keycloak", matchIfMissing = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class SpringKeycloakConfiguration extends KeycloakWebSecurityConfigurerAdapter {

    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        // required for bearer-only applications.
        return new NullAuthenticatedSessionStrategy();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)  {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        // simple Authority Mapper to avoid ROLE_
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }


    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
                // disable csrf because of API mode
                .csrf().disable().sessionManagement()
                // use previously declared bean
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy()).sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // keycloak filters for securisation
                .and().addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
                .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class).exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint()).and()

                // manage routes securisation here
                .authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
                // configuration pour endpoint sans sécurité keycloak
                .antMatchers("/swagger-ui/**","/swagger-ui.html", "/v3/api-docs/**").permitAll()
                // configuration pour public
                .antMatchers("/healthcheck").permitAll()
                .antMatchers("/example/**").permitAll()
                .anyRequest().authenticated();

    }

    @Bean
    public KeycloakConfigResolver keycloakConfigResolverWithProperties(KeycloakSpringBootProperties properties) {
        return new MyKeycloakConfigSpringBootResolverConfiguration(properties);
    }


    @KeycloakConfiguration
    public static class MyKeycloakConfigSpringBootResolverConfiguration extends KeycloakSpringBootConfigResolver {
        private final KeycloakDeployment keycloakDeployment;

        public MyKeycloakConfigSpringBootResolverConfiguration(KeycloakSpringBootProperties properties) {
            keycloakDeployment = KeycloakDeploymentBuilder.build(properties);
        }

        @Override
        public KeycloakDeployment resolve(HttpFacade.Request facade) {
            return keycloakDeployment;

        }
    }

}
