package de.ikor.sip.testkit.workflow.whenphase.executor.impl;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.testkit.util.SIPRouteProducerTemplate;
import de.ikor.sip.testkit.workflow.whenphase.executor.Executor;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/** Class for executing CAMEL requests */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultTestExecutor implements Executor {
  private final SIPRouteProducerTemplate sipRouteProducerTemplate;

  @Override
  public Exchange execute(Exchange exchange, String testName) {
    log.info("Starting CAMEL request execution...");
    Map<String, Object> headers = exchange.getMessage().getHeaders();
    enrichWithTestHeaders(headers, testName);

    return sipRouteProducerTemplate.requestOnRoute(exchange);
  }

  private void enrichWithTestHeaders(Map<String, Object> headers, String testName) {
    headers.put(Executor.TEST_NAME_HEADER, testName);
    headers.put(ProcessorProxy.TEST_MODE_HEADER, true);
  }
}
