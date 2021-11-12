package de.ikor.sip.foundation.security.autoconfigure;

import de.ikor.sip.foundation.core.util.YamlPropertSourceFactory;
import de.ikor.sip.foundation.security.config.SecurityConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Spring-boot autoconfiguration entrypoint (referenced by the <code>
 * src/main/resource/META-INF/spring.factories</code> file.
 *
 * @author thomas.stieglmaier
 */
@Configuration
@ConditionalOnClass(SecurityConfig.class)
@ComponentScan("de.ikor.sip.foundation.security")
@PropertySource(
    value = "classpath:sip-security-default-config.yaml",
    factory = YamlPropertSourceFactory.class)
public class SIPSecurityAutoConfiguration {}
