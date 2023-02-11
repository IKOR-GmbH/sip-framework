package de.ikor.sip.foundation.core.declarative.annonation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * Annotation for beans that represent a connector that links an (external) system with one or more
 * {@link IntegrationScenario}s.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface ConnectorGroup {

  /** @return The ID of the connector. Must be unique among all connectors within the adapter. */
  String groupId();

  /**
   * Optional path to the resource (typically a markdown file) that describes this connector. If not
   * given, an attempt is made to retrieve documentation from <code>
   * document/structure/connector-groups/&lt;connector-ID&gt;</code> automatically.
   *
   * @return Optional path to documentation resource file
   */
  String pathToDocumentationResource() default "";
}
