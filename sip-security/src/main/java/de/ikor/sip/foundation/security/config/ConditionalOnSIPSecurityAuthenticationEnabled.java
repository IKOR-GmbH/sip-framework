package de.ikor.sip.foundation.security.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Conditional annotation for enabled/disabling sip securitie's authentication feature
 *
 * @author thomas.stieglmaier
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ConditionalOnProperty(name = "sip.security.authentication.enabled")
public @interface ConditionalOnSIPSecurityAuthenticationEnabled {}
