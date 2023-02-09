package de.ikor.sip.foundation.core.declarative.annonation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a bean that defines an integration scenario.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface IntegrationScenario {

    /**
     * @return The ID of this integration scenario. Must be unique within the whole adapter.
     */
    String scenarioId();

    Class<?> requestModel();

    Class<?> responseModel() default Void.class;

    /**
     * TODO: update javadocs Optional path to the resource (typically a markdown file) that describes
     * this scenario. If not given, an attempt is made to retrieve documentation from <code>
     * docs/integration-scenarios/&lt;scenario-ID&gt;</code> automatically.
     *
     * @return Optional path to documentation resource file
     */
    String pathToDocumentationResource() default "";
}
