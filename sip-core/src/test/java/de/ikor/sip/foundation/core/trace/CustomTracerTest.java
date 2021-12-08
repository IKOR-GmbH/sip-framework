package de.ikor.sip.foundation.core.trace;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

class CustomTracerTest {

  CustomTracer customTracer;
  TraceHistory traceHistory;
  NamedNode node;
  Exchange exchange;
  ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    traceHistory = new TraceHistory(5);
    customTracer =
        new CustomTracer(traceHistory, null, mock(CamelContext.class), SIPTraceTypeEnum.BOTH);
    node = mock(NamedNode.class);
    exchange = mock(Exchange.class, RETURNS_DEEP_STUBS);

    Logger logger = (Logger) LoggerFactory.getLogger("org.apache.camel.Tracing");
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @Test
  void dumpTrace() {
    List<ILoggingEvent> logsList = listAppender.list;

    customTracer.dumpTrace("1");

    assertThat(logsList.get(0).getMessage()).isEqualTo("1");
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getAndClearHistory()).isNotEmpty();
  }

  @Test
  void dumpTraceTypeZero() {

    List<ILoggingEvent> logsList = listAppender.list;

    customTracer =
        new CustomTracer(traceHistory, null, mock(CamelContext.class), SIPTraceTypeEnum.BOTH);

    customTracer.dumpTrace("0");

    assertThat(logsList).isNotEmpty();
    assertThat(traceHistory.getList()).isNotEmpty();
  }

  @Test
  void dumpTraceTypeOne() {

    List<ILoggingEvent> logsList = listAppender.list;

    customTracer =
        new CustomTracer(traceHistory, null, mock(CamelContext.class), SIPTraceTypeEnum.LOG);

    customTracer.dumpTrace("1");

    assertThat(logsList).isNotEmpty();
    assertThat(traceHistory.getList()).isEmpty();
  }

  @Test
  void dumpTraceTypeTwo() {

    List<ILoggingEvent> logsList = listAppender.list;

    customTracer =
        new CustomTracer(traceHistory, null, mock(CamelContext.class), SIPTraceTypeEnum.MEMORY);

    customTracer.dumpTrace("2");

    assertThat(logsList).isEmpty();
    assertThat(traceHistory.getList()).isNotEmpty();
  }
}
