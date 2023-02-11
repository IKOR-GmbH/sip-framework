package de.ikor.sip.foundation.core.declarative.annonation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface InboundConnector {

  String connectorId() default "";

  String belongsToGroup();

  String toScenario();

  Class<?> requestModel();

  Class<?> responseModel() default Void.class;

  String[] domains() default {};

  String pathToDocumentationResource() default "";
}
