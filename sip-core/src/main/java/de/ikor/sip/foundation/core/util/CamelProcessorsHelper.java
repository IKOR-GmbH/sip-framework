package de.ikor.sip.foundation.core.util;

import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.apache.camel.EndpointAware;
import org.apache.camel.Processor;
import org.apache.camel.processor.*;
import org.apache.commons.lang3.StringUtils;

/** Utility class to help working with specific Camel's processors. */
@UtilityClass
public class CamelProcessorsHelper {

  private static final String[] NON_OUTGOING_PROCESSOR_PREFIXES = {"seda", "direct", "sipmc"};

  public static boolean isEndpointProcessor(Processor originalProcessor) {
    if (originalProcessor instanceof Enricher enricher) {
      if (enricher.getExpression() != null) {
        return isNotInMemoryComponent(enricher.getExpression().toString());
      }
      return true;
    }

    if (originalProcessor instanceof PollEnricher pollEnricher) {
      if (pollEnricher.getExpression() != null) {
        return isNotInMemoryComponent(pollEnricher.getExpression().toString());
      }
      return true;
    }

    if (originalProcessor instanceof WireTapProcessor wireTapProcessor) {
      return isNotInMemoryComponent(wireTapProcessor.getUri());
    }

    if (originalProcessor instanceof EndpointAware endpointAware
        && isNotInMemoryComponent(endpointAware.getEndpoint().getEndpointUri())) {
      return true;
    }

    return originalProcessor instanceof SendDynamicProcessor;
  }

  public static boolean isNotInMemoryComponent(String endpointUri) {
    return !StringUtils.startsWithAny(endpointUri, NON_OUTGOING_PROCESSOR_PREFIXES);
  }

  public static Optional<String> getEndpointUri(Processor processor) {
    if (processor instanceof EndpointAware endpointAware) {
      return Optional.of(endpointAware.getEndpoint().getEndpointBaseUri());
    }
    if (processor instanceof SendDynamicProcessor dynamicProcessor) {
      return Optional.of(dynamicProcessor.getUri());
    }
    if (processor instanceof WireTapProcessor wireTapProcessor) {
      return Optional.of(wireTapProcessor.getUri());
    }
    return Optional.empty();
  }
}
