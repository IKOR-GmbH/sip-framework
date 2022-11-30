package de.ikor.sip.foundation.core.framework.routers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.stereotype.Component;

@Component
@Retention(RetentionPolicy.RUNTIME)
public @interface CentralRouterDomainModel {
  Undefined undefined = new Undefined() {};

  Class<?> requestType() default String.class;

  Class<?> responseType() default Undefined.class;

  interface Undefined {}
}
