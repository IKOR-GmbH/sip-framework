package de.ikor.sip.foundation.core.premiumsupport.registration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SIPBackendRestTemplateFactory {
    @Bean
    public RestTemplate sipBackendRestTemplate(RegistrationConfigurationProperties props) {
        return new RestTemplateBuilder()
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setConnectTimeout(props.getConnectTimeout())
                        .setReadTimeout(props.getReadTimeout())
                        .build();
    }
}
