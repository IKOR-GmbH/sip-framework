package de.ikor.sip.foundation.core.trace;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.support.processor.DefaultExchangeFormatterConfigurer;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Endpoint to get TraceHistory */
@Component
@RequiredArgsConstructor
@RestControllerEndpoint(id = "tracing")
public class TrafficTracerController {

  private final TraceHistory traceHistory;
  private final CamelContext camelContext;

  /**
   * Returns the trace history
   *
   * @return the trace history
   */
  @GetMapping
  @Operation(summary = "Get trace history", description = "Get trace history and clear storage")
  public List<String> getTraceHistory() {
    return traceHistory.getAndClearHistory();
  }

  /**
   * Change a property in DefaultExchangeFormatter in Tracer
   *
   * @param parameter name of parameter
   * @param value value of parameter
   * @return true if parameter is changed, otherwise false
   */
  @PostMapping("/format/{parameter}")
  @Operation(
      summary = "Set ExchangeFormatter parameter value",
      description = "Sets the value of a parameter in ExchangeFormatter for Trace logs")
  public boolean changeParameter(
      @Parameter(name = "parameter", description = "Parameter name") @PathVariable String parameter,
      @Parameter(name = "value", description = "Parameter value") @RequestBody Object value) {
    DefaultExchangeFormatterConfigurer configurer = new DefaultExchangeFormatterConfigurer();
    return configurer.configure(
        camelContext, camelContext.getTracer().getExchangeFormatter(), parameter, value, true);
  }
}
