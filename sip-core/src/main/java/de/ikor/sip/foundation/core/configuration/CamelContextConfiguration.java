package de.ikor.sip.foundation.core.configuration;

import de.ikor.sip.foundation.core.util.ExtendedEventFactory;
import org.apache.camel.CamelContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelContextConfiguration {
  public CamelContextConfiguration(CamelContext camelContext) {
    camelContext.getManagementStrategy().setEventFactory(new ExtendedEventFactory());
  }
}
