package de.ikor.sip.foundation.core;

import de.ikor.sip.foundation.core.premiumsupport.registration.AdapterRegistration;
import de.ikor.sip.foundation.core.util.YamlPropertSourceFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/** Configuration class for scanning beans in package de.ikor.sip.foundation.core */
@Configuration
@PropertySource(
    value = "classpath:sip-core-default-config.yaml",
    factory = YamlPropertSourceFactory.class)
@ComponentScan(
    // Section contains all packages enabled by default
    basePackages = {
      "de.ikor.sip.foundation.core.actuator",
      "de.ikor.sip.foundation.core.annotation",
      "de.ikor.sip.foundation.core.proxies",
      "de.ikor.sip.foundation.core.trace",
      "de.ikor.sip.foundation.core.translate",
      "de.ikor.sip.foundation.core.util"
    })
// @Import contains classes with conditional @ComponentScan. This way certain packages are loaded on
// demand,
// based on configuration.
@Import({AdapterRegistration.class})
public class SIPCoreConfiguration {}
