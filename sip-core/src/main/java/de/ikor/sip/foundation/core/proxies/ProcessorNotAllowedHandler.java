package de.ikor.sip.foundation.core.proxies;

import org.apache.camel.builder.RouteConfigurationBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessorNotAllowedHandler extends RouteConfigurationBuilder {
  @Override
  public void configuration() throws Exception {
    routeConfiguration()
        .onException(ProcessorNotAllowedException.class)
        .log("Aggregator and resequencer are not allowed in SIP Tests!")
        .stop();
  }
}
