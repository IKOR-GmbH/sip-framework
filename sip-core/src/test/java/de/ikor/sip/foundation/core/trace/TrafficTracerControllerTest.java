package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;
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

// TODO update tests
class TrafficTracerControllerTest {

//  private TrafficTracerController endpointSubject;
//  private TraceHistory traceHistory;
//  private CamelContext camelContext;
//
//  @BeforeEach
//  public void setUp() throws Exception {
//    camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
//    traceHistory = new TraceHistory(5);
//    endpointSubject = new TrafficTracerController(traceHistory, camelContext);
//  }
//
//  @Test
//  void When_changeExistingParameter_Expect_returnTrue() throws NoTypeConversionAvailableException {
//    // arrange
//    when(camelContext.getTracer().getExchangeFormatter()).thenReturn(new SIPExchangeFormatter());
//    when(camelContext.getTypeConverter().mandatoryConvertTo(any(), any())).thenReturn(true);
//
//    // assert
//    assertThat(endpointSubject.changeParameter("showexchangeid", "true")).isTrue();
//  }
//
//  @Test
//  void When_changeNonExistingParameter_Expect_returnFalse()
//      throws NoTypeConversionAvailableException {
//    // arrange
//    when(camelContext.getTracer().getExchangeFormatter()).thenReturn(new SIPExchangeFormatter());
//
//    // assert
//    assertThat(endpointSubject.changeParameter("doesnotexist", "true")).isFalse();
//  }
//
//  @Test
//  void When_getTraceHistory_Expect_tracyHistoryList() {
//    // arrange
//    String expectedValue = UUID.randomUUID().toString();
//    traceHistory.add(expectedValue);
//
//    // act
//    List<String> result = endpointSubject.getTraceHistory();
//
//    // assert
//    assertThat(result).containsExactly(expectedValue);
//  }
}
