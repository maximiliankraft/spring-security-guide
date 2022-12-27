package at.maxkraft.restsec.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Configurator to log all the requests incoming to the server
 * for later analysis with e.g. an anomaly detection
 */
@Configuration
public class LoggingConfig {
    @Bean
    CommonsRequestLoggingFilter commonsRequestLoggingFilterBean(){
        CommonsRequestLoggingFilter commonsRequestLoggingFilter = new CommonsRequestLoggingFilter();
        commonsRequestLoggingFilter.setBeanName("Request logger");
        commonsRequestLoggingFilter.setIncludeClientInfo(true);
        commonsRequestLoggingFilter.setIncludeQueryString(true);
        commonsRequestLoggingFilter.setIncludeHeaders(true);
        commonsRequestLoggingFilter.setIncludePayload(true);
        commonsRequestLoggingFilter.setMaxPayloadLength(512);
        return commonsRequestLoggingFilter;
    }
}
