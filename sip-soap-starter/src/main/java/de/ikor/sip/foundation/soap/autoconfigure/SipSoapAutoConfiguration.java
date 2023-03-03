package de.ikor.sip.foundation.soap.autoconfigure;

import de.ikor.sip.foundation.core.util.YamlPropertSourceFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("de.ikor.sip.foundation.soap")
@PropertySource(
    value = "classpath:sip-soap-default-config.yaml",
    factory = YamlPropertSourceFactory.class)
public class SipSoapAutoConfiguration {}
