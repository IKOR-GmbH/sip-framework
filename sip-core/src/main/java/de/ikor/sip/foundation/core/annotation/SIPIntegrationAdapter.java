package de.ikor.sip.foundation.core.annotation;

import de.ikor.sip.foundation.core.SIPCoreConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * Puts together {@link SpringBootApplication} and all other annotations typically needed when
 * developing SIP Integration Adapter.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootApplication
@Import(SIPCoreConfiguration.class)
public @interface SIPIntegrationAdapter {

  /**
   * Alias for exclude in {@link SpringBootApplication}
   *
   * @return class
   */
  @AliasFor(annotation = SpringBootApplication.class)
  Class<?>[] exclude() default {};

  /**
   * Alias for excludeName in {@link SpringBootApplication}
   *
   * @return class
   */
  @AliasFor(annotation = SpringBootApplication.class)
  String[] excludeName() default {};

  /**
   * Alias for basePackages in {@link SpringBootApplication}
   *
   * @return class
   */
  @AliasFor(annotation = SpringBootApplication.class, attribute = "scanBasePackages")
  String[] basePackages() default {};

  /**
   * Alias for proxyBeanMethods in {@link SpringBootApplication}
   *
   * @return class
   */
  @AliasFor(annotation = SpringBootApplication.class)
  boolean proxyBeanMethods() default true;
}
