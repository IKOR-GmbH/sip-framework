package de.ikor.sip.foundation.core.actuator.health.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ConditionalOnExpression(
        "${sip.core.metrics.scheduled-health-check.enabled:true} and '${management.endpoints.web.exposure.include}'.contains('health')")
public @interface HealthCheckEnabledCondition {
}
