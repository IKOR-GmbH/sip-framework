package de.ikor.sip.testframework;

import de.ikor.sip.foundation.core.util.YamlPropertSourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import(TestKitConfig.class)
@Configuration
@PropertySource(
    value = "classpath:sip-testkit-default-config.yaml",
    factory = YamlPropertSourceFactory.class)
public class SIPTestKitAutoConfiguration {}
