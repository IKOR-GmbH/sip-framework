package de.ikor.sip.foundation.core.proxies;

import java.util.function.UnaryOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.camel.Exchange;

/**
 * {@link ProcessorProxyMock} is a simple POJO class used for ProcessorProxy {@link ProcessorProxy} mock function.
 */
@Getter
@AllArgsConstructor
public class ProcessorProxyMock {

  private String processorId;

  private UnaryOperator<Exchange> mockFunction;
}
