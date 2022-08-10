package de.ikor.sip.foundation.core.trace;

import de.ikor.sip.foundation.core.trace.model.TraceUnit;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

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
  public List<TraceUnit> getTraceHistory() {
    return traceHistory.getAndClearHistory();
  }
}
