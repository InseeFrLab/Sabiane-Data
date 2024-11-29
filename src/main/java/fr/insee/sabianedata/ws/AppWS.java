package fr.insee.sabianedata.ws;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

@SpringBootApplication(scanBasePackages = "fr.insee.sabianedata.ws")
public class AppWS extends SpringBootServletInitializer {

    public static final String APP_NAME = "sabdatab";

    private static final Logger log = LoggerFactory.getLogger(AppWS.class);

    public static void main(String[] args) {
        System.setProperty("spring.config.name", APP_NAME);
        SpringApplication.run(AppWS.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.setProperty("spring.config.name", APP_NAME);
        setProperty();
        return application.sources(AppWS.class);
    }

    public static void setProperty() {
        System.setProperty("spring.config.location",
                "classpath:/," + "file:///${catalina.base}/webapps/" + APP_NAME + ".properties");
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();
        log.info("================================ Properties ================================");
        final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
        StreamSupport.stream(sources.spliterator(), false).filter(EnumerablePropertySource.class::isInstance)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames()).flatMap(Arrays::stream).distinct()
                .filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
                .filter(prop -> prop.startsWith("fr.insee") || prop.startsWith("logging") || prop.startsWith("spring")
                        || prop.startsWith("keycloak"))
                .sorted().forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));
        log.info("===========================================================================");
        log.info("Available CPU : {}", Runtime.getRuntime().availableProcessors());
        log.info(String.format("Max memory : %.2f GB", Runtime.getRuntime().maxMemory() / 1e9d));
        log.info("===========================================================================");
    }

    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        log.info("=============== " + APP_NAME + "  has successfully started. ===============");

    }
}
