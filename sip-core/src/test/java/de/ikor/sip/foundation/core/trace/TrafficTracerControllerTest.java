package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import de.ikor.sip.foundation.core.trace.model.TraceUnit;
import java.util.List;
import org.apache.camel.CamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrafficTracerControllerTest {

  private TrafficTracerController endpointSubject;
  private TraceHistory traceHistory;
  private CamelContext camelContext;

  @BeforeEach
  public void setUp() throws Exception {
    camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    traceHistory = new TraceHistory(5);
    endpointSubject = new TrafficTracerController(traceHistory, camelContext);
  }

  @Test
  void When_getTraceHistory_Expect_traceHistoryList() {
    // arrange
    TraceUnit expectedValue = new TraceUnit();
    traceHistory.add(expectedValue);

    // act
    List<TraceUnit> result = endpointSubject.getTraceHistory();

    // assert
    assertThat(result).containsExactly(expectedValue);
  }
}
