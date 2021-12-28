package de.ikor.sip.foundation.core.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import de.ikor.sip.foundation.core.premiumsupport.registration.SIPBackendRegistrationConfig;

@Configuration
@Import(SIPBackendRegistrationConfig.class)
public class SIPPremiumConfiguration {
}