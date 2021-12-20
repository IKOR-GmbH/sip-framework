package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.apache.camel.CamelContext;
import org.apache.camel.NoTypeConversionAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrafficTracerControllerTest {

  private TrafficTracerController endpoint;

  private TraceHistory traceHistory;

  @BeforeEach
  public void setUp() throws Exception {
    CamelContext camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    traceHistory = new TraceHistory(5);
    endpoint = new TrafficTracerController(traceHistory, camelContext);
  }

  @Test
  void When_changeParameter_Expect_returnTrue() throws NoTypeConversionAvailableException {
    CamelContext camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    TrafficTracerController tracingController =
        new TrafficTracerController(traceHistory, camelContext);
    when(camelContext.getTracer().getExchangeFormatter()).thenReturn(new SIPExchangeFormatter());
    when(camelContext.getTypeConverter().mandatoryConvertTo(any(), any())).thenReturn(true);

    assertTrue(tracingController.changeParameter("showexchangeid", true));

    assertFalse(tracingController.changeParameter("doesnotexist", true));
  }

  @Test
  void When_getTraceHistory_Expect_tracyHistoryList() {
    // arrange
    String expectedValue = UUID.randomUUID().toString();
    traceHistory.add(expectedValue);

    // act
    List<String> result = endpoint.getTraceHistory();

    // assert
    assertThat(result).containsExactly(expectedValue);
  }

  @Test
  void When_removeTraceHistory_Expect_emptyTractHistoryList() {
    // arrange
    traceHistory.add("Test");

    // act
    endpoint.getTraceHistory();

    // assert
    assertThat(endpoint.getTraceHistory()).isEmpty();
  }
}
