package de.ikor.sip.foundation.core.proxies;

import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.processor.SendDynamicProcessor;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Apache Camel Processor creation interception */
@Slf4j
@Component
@RequiredArgsConstructor
public class AddProxyInterceptStrategy implements InterceptStrategy {
  private final ProcessorProxyRegistry proxyRegistry;
  private final List<ProxyExtension> extensions;
  private static final String[] IGNORED_ENDPOINT_PREFIXES = {"seda", "direct", "sipmc"};

  @Override
  public Processor wrapProcessorInInterceptors(
      CamelContext context, NamedNode definition, Processor target, Processor nextTarget)
      throws Exception {
    String processorId = definition.getId();
    log.info("sip.core.proxy.register.info_{}", processorId);

    // nextTarget is the original processor, target is Camel's wrapped WrapProcessor
    ProcessorProxy processorProxy =
        new ProcessorProxy(definition, target, isEndpointProcessor(nextTarget), extensions);
    proxyRegistry.register(processorId, processorProxy);
    return processorProxy;
  }

  private boolean isEndpointProcessor(Processor target) {
    if (target instanceof EndpointAware) {
      Endpoint destinationEndpoint = ((EndpointAware) target).getEndpoint();
      if (!StringUtils.startsWithAny(
          destinationEndpoint.getEndpointUri(), IGNORED_ENDPOINT_PREFIXES)) {
        return true;
      }
    }
    return target instanceof SendDynamicProcessor;
  }
}
