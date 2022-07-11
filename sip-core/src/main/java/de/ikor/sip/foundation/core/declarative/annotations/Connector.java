package de.ikor.sip.foundation.core.declarative.annotations;

import de.ikor.sip.foundation.core.declarative.definitions.ConnectorDefinition;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * Annotation for beans that represent a connector that links an (external) system with one or more
 * {@link IntegrationScenario}s.
 *
 * <p>Must only be used on a bean implementing {@link ConnectorDefinition}.
 *
 * @see ScenarioParticipationIncoming
 * @see ScenarioParticipationOutgoing
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Connector {

  /** @return The ID of the connector. Must be unique among all connectors within the adapter. */
  String value();

  /**
   * Optional path to the resource (typically a markdown file) that describes this connector. If not
   * given, an attempt is made to retrieve documentation from <code>
   * docs/connectors/&lt;connector-ID&gt;</code> automatically.
   *
   * @return Optional path to documentation resource file
   */
  String pathToDocumentationResource() default "";
}
