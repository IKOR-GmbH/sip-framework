package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class CustomTracerTest {

  private static final String LOG_MESSAGE = "log message";

  @Test
  void When_dumpTrace_Expect_messageInLog() {
    // arrange
    TraceHistory traceHistory = new TraceHistory(5);
    CustomTracer customTracer = new CustomTracer(traceHistory, null, mock(CamelContext.class));
    Logger logger = (Logger) LoggerFactory.getLogger("org.apache.camel.Tracing");
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    customTracer.dumpTrace(LOG_MESSAGE);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo(LOG_MESSAGE);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getAndClearHistory()).containsExactly(LOG_MESSAGE);
  }
}
