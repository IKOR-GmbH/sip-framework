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
  CustomTracer subject;
  TraceHistory traceHistory;
  ListAppender<ILoggingEvent> listAppender;
  SIPTraceConfig traceConfig;
  Set<SIPTraceOperation> sipTraceOperationSet;
  Exchange exchange;

  @BeforeEach
  void setUp() {
    exchange = mock(Exchange.class);
    sipTraceOperationSet = new LinkedHashSet<>();
    traceHistory = new TraceHistory(5);
    traceConfig = new SIPTraceConfig();

    Logger logger = (Logger) LoggerFactory.getLogger("org.apache.camel.Tracing");
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @Test
  void When_dumpTrace_With_LogAndMemory_Expect_messageInLogAndHistory() {
    // arrange
    sipTraceOperationSet.add(SIPTraceOperation.LOG);
    sipTraceOperationSet.add(SIPTraceOperation.MEMORY);
    subject = new CustomTracer(traceHistory, null, mock(CamelContext.class), sipTraceOperationSet);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.dumpTrace(LOG_MESSAGE, null);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo(LOG_MESSAGE);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getAndClearHistory()).containsExactly(LOG_MESSAGE);
  }

  @Test
  void When_dumpTrace_With_LOG_Expect_messageInLog() {
    // arrange
    sipTraceOperationSet.add(SIPTraceOperation.LOG);
    subject = new CustomTracer(traceHistory, null, mock(CamelContext.class), sipTraceOperationSet);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.dumpTrace(LOG_MESSAGE, null);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo(LOG_MESSAGE);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getList()).isEmpty();
  }

  @Test
  void When_dumpTrace_With_MEMORY_Expect_messageInLog() {
    // arrange
    sipTraceOperationSet.add(SIPTraceOperation.MEMORY);
    subject = new CustomTracer(traceHistory, null, mock(CamelContext.class), sipTraceOperationSet);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.dumpTrace(LOG_MESSAGE, null);

    // assert
    assertThat(logsList).isEmpty();
    assertThat(traceHistory.getAndClearHistory()).containsExactly(LOG_MESSAGE);
  }

  @Test
  void When_traceBeforeNode_Then_setTracingId() {
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

  private void initTracingIDTest() {
    subject = new CustomTracer(traceHistory, null, mock(CamelContext.class), sipTraceOperationSet);
    subject.setEnabled(false);
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    when(camelContext.getHeadersMapFactory()).thenReturn(null);
    Message message = new DefaultMessage(camelContext);
    when(exchange.getIn()).thenReturn(message);
    when(exchange.getContext()).thenReturn(camelContext);
    when(exchange.getExchangeId()).thenReturn(EXCHANGE_ID);
  }
}
