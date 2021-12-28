package de.ikor.sip.foundation.core.premiumsupport.registration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * This class adds registration package to Spring component scan path.
 */
@Configuration
@ComponentScan()
@ConditionalOnProperty(name = "sip.core.backend-registration.enabled", havingValue = "true")
public class SIPBackendRegistrationConfig {
}
