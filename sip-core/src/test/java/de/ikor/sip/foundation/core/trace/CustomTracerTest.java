package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.ikor.sip.foundation.core.trace.model.ExchangeTracePoint;
import de.ikor.sip.foundation.core.trace.model.TraceUnit;
import java.util.List;
import org.apache.camel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

class CustomTracerTest {

  private static final String EXCHANGE_ID = "000";
  private static final String FROM_ID = "fromId";

  CustomTracer subject;
  SIPExchangeFormatter exchangeFormatter;
  Exchange exchange;
  TraceHistory traceHistory;
  ListAppender<ILoggingEvent> listAppender;
  SIPTraceConfig traceConfig;
  CamelContext context;

  @BeforeEach
  void setUp() {

    exchangeFormatter = mock(SIPExchangeFormatter.class);
    exchange = mock(ExtendedExchange.class);
    context = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    when(exchange.getExchangeId()).thenReturn(EXCHANGE_ID);
    when(exchange.getFromRouteId()).thenReturn(FROM_ID);
    traceHistory = new TraceHistory(5);
    traceConfig = new SIPTraceConfig();

    Logger logger = (Logger) LoggerFactory.getLogger("org.apache.camel.Tracing");
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @Test
  void When_traceBeforeNode_With_StoreInMemory_Expect_messageInLogAndHistory() {
    // arrange
    NamedNode node = mock(NamedNode.class);

    Message message = mock(Message.class);
    when(exchange.getContext()).thenReturn(context);
    when(exchange.getMessage()).thenReturn(message);
    when(exchange.getIn()).thenReturn(message);
    when(context.getTypeConverter().convertTo(any(), any(), any())).thenReturn(FROM_ID);
    subject = new CustomTracer(traceHistory, exchangeFormatter, context);
    ReflectionTestUtils.setField(subject, "shouldStore", true);
    List<ILoggingEvent> logsList = listAppender.list;
    TraceUnit traceUnit = new TraceUnit();
    traceUnit.setExchangeId(EXCHANGE_ID);

    // act
    subject.traceBeforeNode(node, exchange);

    // assert
    assertThat(logsList.get(0).getMessage()).contains(FROM_ID);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getAndClearHistory()).containsExactly(traceUnit);
  }

  @Test
  void When_traceBeforeNode_With_WithException_Expect_messageInLogAndHistory() {
    // arrange
    String exception_message = "exception message";
    NamedNode node = mock(NamedNode.class);
    Exception exception = mock(Exception.class);
    Message message = mock(Message.class);
    when(exchange.getContext()).thenReturn(context);
    when(exchange.getMessage()).thenReturn(message);
    when(exchange.getIn()).thenReturn(message);
    when(exchange.getException()).thenReturn(exception);
    when(exception.getMessage()).thenReturn(exception_message);
    when(context.getTypeConverter().convertTo(any(), any(), any())).thenReturn(FROM_ID);
    subject = new CustomTracer(traceHistory, exchangeFormatter, context);
    ReflectionTestUtils.setField(subject, "shouldStore", true);
    List<ILoggingEvent> logsList = listAppender.list;
    TraceUnit traceUnit = new TraceUnit();
    traceUnit.setExchangeId(EXCHANGE_ID);
    traceUnit.setExceptionMessage(exception_message);
    traceUnit.setExceptionType(exception.getClass().getCanonicalName());
    traceUnit.setStackTrace("");

    // act
    subject.traceBeforeNode(node, exchange);

    // assert
    assertThat(logsList.get(0).getMessage()).contains(FROM_ID);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getAndClearHistory()).containsExactly(traceUnit);
  }

  @Test
  void When_traceBeforeRoute_With_StoreInMemory_Expect_messageInLogAndHistory() {
    // arrange
    NamedRoute node = mock(NamedRoute.class);
    CamelContext context = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    Message message = mock(Message.class);
    when(exchange.getContext()).thenReturn(context);
    when(exchange.getMessage()).thenReturn(message);
    when(exchange.getIn()).thenReturn(message);
    when(node.getRouteId()).thenReturn(FROM_ID);
    subject = new CustomTracer(traceHistory, exchangeFormatter, context);
    subject.setCamelContext(context);
    ReflectionTestUtils.setField(subject, "shouldStore", true);
    List<ILoggingEvent> logsList = listAppender.list;
    TraceUnit traceUnit = new TraceUnit();
    traceUnit.setExchangeId(EXCHANGE_ID);
    traceUnit.setNodeId(FROM_ID);
    traceUnit.setTracePoint(ExchangeTracePoint.START);

    // act
    subject.traceBeforeRoute(node, exchange);

    // assert
    assertThat(logsList.get(0).getMessage()).contains(FROM_ID);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getAndClearHistory()).containsExactly(traceUnit);
  }

  @Test
  void When_traceAfterRoute_With_StoreInMemory_Expect_messageInLogAndHistory() {
    // arrange
    NamedRoute node = mock(NamedRoute.class);
    CamelContext context = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    Message message = mock(Message.class);
    when(exchange.getContext()).thenReturn(context);
    when(exchange.getMessage()).thenReturn(message);
    when(exchange.getIn()).thenReturn(message);
    when(node.getRouteId()).thenReturn(FROM_ID);
    subject = new CustomTracer(traceHistory, exchangeFormatter, context);
    subject.setCamelContext(context);
    ReflectionTestUtils.setField(subject, "shouldStore", true);
    List<ILoggingEvent> logsList = listAppender.list;
    TraceUnit traceUnit = new TraceUnit();
    traceUnit.setExchangeId(EXCHANGE_ID);
    traceUnit.setNodeId(FROM_ID);
    traceUnit.setTracePoint(ExchangeTracePoint.DONE);

    // act
    subject.traceAfterRoute(node, exchange);

    // assert
    assertThat(logsList.get(0).getMessage()).contains(FROM_ID);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getAndClearHistory()).containsExactly(traceUnit);
  }
}
