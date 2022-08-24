package de.ikor.sip.foundation.testkit.workflow.whenphase;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

/** Executes WhenPhaseDefinition */
@Slf4j
@Data
@AllArgsConstructor
public class ExecutionWrapper {

  private String testName;
  private Exchange whenDefinitionExchange;
  private final RouteInvoker invoker;

  /**
   * WhenPhaseDefinition
   *
   * @return {@link Exchange}
   */
  public Optional<Exchange> execute() {
    log.info("sip.testkit.workflow.startcamelrequest");
    enrichWithTestHeaders();
    return invoker.invoke(whenDefinitionExchange);
  }

  private void enrichWithTestHeaders() {
    Map<String, Object> headers = whenDefinitionExchange.getMessage().getHeaders();
    headers.put(RouteInvoker.TEST_NAME_HEADER, testName);
    headers.put(ProcessorProxy.TEST_MODE_HEADER, true);
  }
}
