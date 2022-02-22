package de.ikor.sip.foundation.core.proxies;

import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/** Configuration for ProcessorProxy */
@Slf4j
@ComponentScan
@Configuration
@AllArgsConstructor
@ConditionalOnProperty(value = "sip.core.proxy.enabled", havingValue = "true")
public class ProcessorProxyConfiguration {
  private final CamelContext camelContext;
  private final ProcessorProxyRegistry proxyRegistry;

  /**
   * Auto executed method for registering {@link ProcessorProxyRegistry} in {@link CamelContext} as
   * extension
   */
  @PostConstruct
  public void addProxyRegistryToCamelContext() {
    log.info("sip.core.camelcontext.register.proxyregistry");
    camelContext.setExtension(ProcessorProxyRegistry.class, proxyRegistry);
  }
}
