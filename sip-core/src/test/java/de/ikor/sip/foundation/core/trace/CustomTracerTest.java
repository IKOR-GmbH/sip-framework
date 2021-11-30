package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

  CustomTracer customTracer;
  TraceHistory traceHistory;
  NamedNode node;
  Exchange exchange;
  ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    traceHistory = new TraceHistory(5);
    customTracer = new CustomTracer(traceHistory, null, mock(CamelContext.class), 0);
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

    assertEquals("1", logsList.get(0).getMessage());
    assertEquals(Level.INFO, logsList.get(0).getLevel());
    assertThat(traceHistory.getAndClearHistory()).isNotEmpty();
  }
}
