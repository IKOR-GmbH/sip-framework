package de.ikor.sip.foundation.core.framework.routers;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface CentralRouterDomainModel {
    Undefined undefined = null;

    Class<?> requestType() default String.class;
    Class<?> responseType() default Undefined.class;

    interface Undefined {
    }
}
