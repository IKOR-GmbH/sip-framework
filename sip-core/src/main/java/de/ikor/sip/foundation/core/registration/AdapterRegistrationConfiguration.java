package de.ikor.sip.foundation.core.registration;

import de.ikor.sip.foundation.core.actuator.routes.AdapterRouteEndpoint;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Configuration in order to load all necessary beans.
 */
@Configuration
@ConditionalOnExpression("${sip.core.backend-registration.enabled:false}")
class AdapterRegistrationConfiguration {

  @Bean
  AdapterRegistration createAdapterRegistration(AdapterRegistrationProperties properties,
                                                Environment environment, HealthEndpoint healthEndpoint,
                                                AdapterRouteEndpoint adapterRouteEndpoint,
                                                PathMappedEndpoints pathMappedEndpoints) {
    return new AdapterRegistration(properties, environment, healthEndpoint, adapterRouteEndpoint, pathMappedEndpoints);
  }

}
