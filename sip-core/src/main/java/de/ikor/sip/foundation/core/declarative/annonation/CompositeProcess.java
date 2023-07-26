package de.ikor.sip.foundation.core.declarative.annonation;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface CompositeProcess {

  String processId();

  String pathToDocumentationResource() default "";

  Class<? extends IntegrationScenarioDefinition>[] consumers();

  Class<? extends IntegrationScenarioDefinition>[] providers();
}
