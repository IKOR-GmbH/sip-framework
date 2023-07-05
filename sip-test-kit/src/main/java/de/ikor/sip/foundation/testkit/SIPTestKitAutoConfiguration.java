package de.ikor.sip.foundation.testkit;

import de.ikor.sip.foundation.core.util.YamlPropertSourceFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import(TestKitConfig.class)
@AutoConfiguration
@PropertySource(
    value = "classpath:sip-testkit-default-config.yaml",
    factory = YamlPropertSourceFactory.class)
public class SIPTestKitAutoConfiguration {}
