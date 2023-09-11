package de.ikor.sip.foundation.core.configuration.errorhandler;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/** Default logging onException definition */
@AutoConfiguration
public class SIPDefaultErrorHandler extends RouteConfigurationBuilder {
  @Override
  public void configuration() throws Exception {
    routeConfiguration()
        .onException(Exception.class)
        .log(LoggingLevel.ERROR, "exception")
        .id("default-sip-error-handler");
  }
}
