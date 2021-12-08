package de.ikor.sip.foundation.core.proxies;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import lombok.Getter;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * {@link ProcessorProxyMockRegistry} is a register which collects mock functions for corresponding
 * processor proxies. Register is used for collecting only mock functions registered during runtime.
 */
@Getter
@Component
public class ProcessorProxyMockRegistry {

  private Map<String, ProcessorProxyMock> proxyMocks = new HashMap<>();

  /**
   * Registers mocking function for the given processor id.
   *
   * @param processorId processor id used for matching with the ProxyProcessor {@link
   *     ProcessorProxy}.
   * @param mockFunction function used as a ProcessorProxy mocking behaviour.
   */
  public void registerMock(String processorId, UnaryOperator<Exchange> mockFunction) {
    if (processorId == null || proxyMocks.containsKey(processorId)) {
      throw new BadProxyProcessorIdException(
          "Registering mock with processor id which is null or already exists");
    }
    ProcessorProxyMock proxyMock = new ProcessorProxyMock(processorId, mockFunction);
    proxyMocks.put(processorId, proxyMock);
  }
}
