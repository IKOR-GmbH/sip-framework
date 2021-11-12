package de.ikor.sip.foundation.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/** Static wrapper around spring application context instance. */
@Slf4j
@Component
public class SIPStaticSpringContext implements ApplicationContextAware {

  private static ApplicationContext context;

  /**
   * Returns the Spring managed bean instance of the given class type if it exists. Returns null
   * otherwise.
   *
   * @param beanClass class type of desired bean
   * @param <T> bean class type
   * @return the Spring managed bean instance if it exists. Null otherwise.
   */
  public static <T> T getBean(Class<T> beanClass) {
    T bean = null;
    try {
      bean = context.getBean(beanClass);
    } catch (Exception e) {
      log.debug("sip.core.util.missingbean_{}", beanClass.getCanonicalName());
    }
    return bean;
  }

  @Override
  public void setApplicationContext(ApplicationContext context) {
    // store ApplicationContext reference to access required beans later on
    synchronized (this) {
      SIPStaticSpringContext.context = context;
    }
  }
}
