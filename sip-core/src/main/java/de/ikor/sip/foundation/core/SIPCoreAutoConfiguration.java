package de.ikor.sip.foundation.core;

import de.ikor.sip.foundation.core.util.YamlPropertSourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Configuration class for scanning beans in package de.ikor.sip.foundation.core */
@Configuration
@PropertySource(
    value = "classpath:sip-core-default-config.yaml",
    factory = YamlPropertSourceFactory.class)
public class SIPCoreAutoConfiguration {}
