package de.ikor.sip.foundation.core.declarative.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that defines that a {@link Connector} participates in a {@link IntegrationScenario}
 * with an incoming role.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScenarioParticipationIncoming {

  /** @return ID of the {@link IntegrationScenario} that this {@link Connector} participates in */
  String value();
}
