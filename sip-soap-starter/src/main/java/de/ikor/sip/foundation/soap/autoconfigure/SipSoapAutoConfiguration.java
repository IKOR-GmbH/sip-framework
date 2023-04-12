package de.ikor.sip.foundation.soap.autoconfigure;

import de.ikor.sip.foundation.core.util.YamlPropertSourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("de.ikor.sip.foundation.soap")
@ConditionalOnProperty(value = "sip.core.declarativestructure.enabled", havingValue = "true")
@PropertySource(
    value = "classpath:sip-soap-default-config.yaml",
    factory = YamlPropertSourceFactory.class)
public class SipSoapAutoConfiguration {}
