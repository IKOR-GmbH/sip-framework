package de.ikor.sip.foundation.core.actuator.health.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ConditionalOnExpression("${sip.core.metrics.external-endpoint-health-check.enabled}")
@ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
public @interface HealthCheckEnabledCondition {}
