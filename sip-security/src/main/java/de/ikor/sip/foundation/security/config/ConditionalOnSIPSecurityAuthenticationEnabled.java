package de.ikor.sip.foundation.security.config;

import de.ikor.sip.foundation.security.authentication.SIPAuthProvidersExistCondition;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;

/**
 * Conditional annotation for enabled/disabling sip securitie's authentication feature
 *
 * @author thomas.stieglmaier
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(SIPAuthProvidersExistCondition.class)
public @interface ConditionalOnSIPSecurityAuthenticationEnabled {}
