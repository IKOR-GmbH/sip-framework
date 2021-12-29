package de.ikor.sip.foundation.core.premiumsupport.registration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
//todo javadocs
class SIPBackendRestTemplateConfig {
    @Bean
    //todo javadocs
    public RestTemplate sipBackendRestTemplate(SIPRegistrationProperties props) {
        return new RestTemplateBuilder()
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setConnectTimeout(props.getConnectTimeout())
                        .setReadTimeout(props.getReadTimeout())
                        .build();
    }
}