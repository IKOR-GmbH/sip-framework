package de.ikor.sip.foundation.core.trace;

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
  public boolean changeParameter(@PathVariable String parameter, @RequestBody Object value) {
    DefaultExchangeFormatterConfigurer configurer = new DefaultExchangeFormatterConfigurer();
    return configurer.configure(
        camelContext, camelContext.getTracer().getExchangeFormatter(), parameter, value, true);
  }
}
