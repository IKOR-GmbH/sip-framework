package de.ikor.sip.foundation.core.declarative.annonations;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface OutboundConnector {

    String connectorId() default "";

    String belongsToGroup();

    String fromScenario();

    String[] domains() default {};
}
