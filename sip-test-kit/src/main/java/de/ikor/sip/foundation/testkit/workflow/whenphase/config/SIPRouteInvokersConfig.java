package de.ikor.sip.foundation.testkit.workflow.whenphase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SIPRouteInvokersConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
