package de.ikor.sip.foundation.core.proxies;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * {@link ProcessorProxyMockSetup} is a class which represents execution after processor proxies are collected.
 */
@Component
@AllArgsConstructor
public class ProcessorProxyMockSetup {
  private final ProcessorProxyRegistry proxyRegistry;
  private final ProcessorProxyMockRegistry proxyMockRegistry;

    /**
     * Mocking ProcessorProxies {@link ProcessorProxy} with their mocking functions.
     */
  @EventListener(ApplicationReadyEvent.class)
  public void mockProcessorProxies() {
    proxyMockRegistry
        .getProxyMocks()
        .forEach(
            (processorId, processorProxyMock) -> {
              ProcessorProxy processorProxy =
                  proxyRegistry
                      .getProxy(processorId)
                      .orElseThrow(
                          () ->
                              new BadProxyProcessorIdException(
                                  "There is no proxy with processor id: " + processorId));
              processorProxy.mock(processorProxyMock.getMockFunction());
            });
  }
}
