package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class CustomTracerTest {

  private static final String LOG_MESSAGE = "log message";
  CustomTracer customTracer;
  TraceHistory traceHistory;
  NamedNode node;
  Exchange exchange;
  ListAppender<ILoggingEvent> listAppender;
  SIPTraceConfig traceConfig;

  @BeforeEach
  void setUp() {
    traceHistory = new TraceHistory(5);
    traceConfig = new SIPTraceConfig();
    traceConfig.setTraceType(SIPTraceTypeEnum.BOTH);
    customTracer = new CustomTracer(traceHistory, null, mock(CamelContext.class), traceConfig);
    node = mock(NamedNode.class);
    exchange = mock(Exchange.class, RETURNS_DEEP_STUBS);

    Logger logger = (Logger) LoggerFactory.getLogger("org.apache.camel.Tracing");
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @Test
  void When_dumpTrace_Expect_messageInLog() {
    // arrange
    TraceHistory traceHistory = new TraceHistory(5);
    CustomTracer subject = new CustomTracer(traceHistory, null, mock(CamelContext.class));
    Logger logger = (Logger) LoggerFactory.getLogger("org.apache.camel.Tracing");
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.dumpTrace(LOG_MESSAGE);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo(LOG_MESSAGE);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getAndClearHistory()).containsExactly(LOG_MESSAGE);
  }

  @Test
  void dumpTraceTypeZero() {

    List<ILoggingEvent> logsList = listAppender.list;

    traceConfig = new SIPTraceConfig();
    traceConfig.setTraceType(SIPTraceTypeEnum.BOTH);

    customTracer = new CustomTracer(traceHistory, null, mock(CamelContext.class), traceConfig);

    customTracer.dumpTrace("0");

    assertThat(logsList).isNotEmpty();
    assertThat(traceHistory.getList()).isNotEmpty();
  }

  @Test
  void dumpTraceTypeOne() {

    List<ILoggingEvent> logsList = listAppender.list;

    traceConfig = new SIPTraceConfig();
    traceConfig.setTraceType(SIPTraceTypeEnum.LOG);
    customTracer = new CustomTracer(traceHistory, null, mock(CamelContext.class), traceConfig);

    customTracer.dumpTrace("1");

    assertThat(logsList).isNotEmpty();
    assertThat(traceHistory.getList()).isEmpty();
  }

  @Test
  void dumpTraceTypeTwo() {

    List<ILoggingEvent> logsList = listAppender.list;

    traceConfig = new SIPTraceConfig();
    traceConfig.setTraceType(SIPTraceTypeEnum.MEMORY);

    customTracer = new CustomTracer(traceHistory, null, mock(CamelContext.class), traceConfig);

    customTracer.dumpTrace("2");

    assertThat(logsList).isEmpty();
    assertThat(traceHistory.getList()).isNotEmpty();
  }
}
