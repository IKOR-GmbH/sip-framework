package de.ikor.sip.foundation.core.trace;

import static de.ikor.sip.foundation.core.trace.CustomTracer.TRACING_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.*;
import org.apache.camel.*;
import org.apache.camel.support.DefaultMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class CustomTracerTest {

  private static final String LOG_MESSAGE = "log message";
  private static final String EXCHANGE_ID = "id";
  private static final String ROUTE_ID = "routeId";
  SIPExchangeFormatter exchangeFormatter;
  CustomTracer subject;
  CamelContext camelContext;
  ListAppender<ILoggingEvent> listAppender;
  SIPTraceConfig traceConfig;
  Exchange exchange;

  @BeforeEach
  void setUp() {
    camelContext = mock(CamelContext.class);
    exchange = mock(Exchange.class);
    traceConfig = new SIPTraceConfig();
    exchangeFormatter = mock(SIPExchangeFormatter.class);
    subject = new CustomTracer(exchangeFormatter, camelContext, traceConfig);
    Logger logger = (Logger) LoggerFactory.getLogger("org.apache.camel.Tracing");
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @Test
  void When_dumpTrace_With_LogsEnabled_Then_messageInLog() {
    // arrange
    traceConfig.setLog(true);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.dumpTrace(LOG_MESSAGE, null);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo(LOG_MESSAGE);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
  }

  @Test
  void When_dumpTrace_With_DisabledLogs_Then_emptyLogs() {
    // arrange
    traceConfig.setLog(false);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.dumpTrace(LOG_MESSAGE, null);

    // assert
    assertThat(logsList).isEmpty();
  }

  @Test
  void When_traceBeforeNode_With_NoIdInHeaders_Then_OneTracingId() {
    // arrange
    initTracingIDTest();

    // act
    subject.traceBeforeNode(mock(NamedNode.class), exchange);

    // assert
    assertThat(exchange.getIn().getHeader(TRACING_ID)).isEqualTo(EXCHANGE_ID);
  }

  @Test
  void When_traceBeforeNode_With_TracingIdExists_Then_concatNewTracingId() {
    // arrange
    String oldId = "old";
    initTracingIDTest();
    exchange.getIn().setHeader(TRACING_ID, oldId);

    // act
    subject.traceBeforeNode(mock(NamedNode.class), exchange);

    // assert
    assertThat(exchange.getIn().getHeader(TRACING_ID)).isEqualTo(oldId + "," + EXCHANGE_ID);
  }

  @Test
  void When_traceBeforeRoute_With_NoIdInHeaders_Then_OneTracingId() {
    // arrange
    NamedRoute namedRoute = mock(NamedRoute.class);
    initTracingIDTest();
    when(exchange.getFromRouteId()).thenReturn(ROUTE_ID);
    when(namedRoute.getRouteId()).thenReturn(ROUTE_ID);
    when(camelContext.isDebugging()).thenReturn(false);
    when(exchangeFormatter.format(exchange)).thenReturn("formatted");
    subject.setCamelContext(camelContext);

    // act
    subject.traceBeforeRoute(namedRoute, exchange);

    // assert
    assertThat(exchange.getIn().getHeader(TRACING_ID)).isEqualTo(EXCHANGE_ID);
  }

  private void initTracingIDTest() {
    subject.setEnabled(false);
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    when(camelContext.getHeadersMapFactory()).thenReturn(null);
    Message message = new DefaultMessage(camelContext);
    when(exchange.getIn()).thenReturn(message);
    when(exchange.getContext()).thenReturn(camelContext);
    when(exchange.getExchangeId()).thenReturn(EXCHANGE_ID);
  }
}
