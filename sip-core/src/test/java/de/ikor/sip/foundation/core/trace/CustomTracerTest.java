package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.camel.CamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class CustomTracerTest {

  private static final String LOG_MESSAGE = "log message";
  CustomTracer subject;
  TraceHistory traceHistory;
  ListAppender<ILoggingEvent> listAppender;
  SIPTraceConfig traceConfig;
  Set<SIPTraceTypeEnum> sipTraceTypeEnumSet;

  @BeforeEach
  void setUp() {
    sipTraceTypeEnumSet = new LinkedHashSet<>();
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
    sipTraceTypeEnumSet.add(SIPTraceTypeEnum.LOG);
    sipTraceTypeEnumSet.add(SIPTraceTypeEnum.MEMORY);
    subject = new CustomTracer(traceHistory, null, mock(CamelContext.class), sipTraceTypeEnumSet);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.dumpTrace(LOG_MESSAGE);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo(LOG_MESSAGE);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getAndClearHistory()).containsExactly(LOG_MESSAGE);
  }

  @Test
  void When_dumpTrace_With_LOG_Expect_messageInLog() {
    // arrange
    sipTraceTypeEnumSet.add(SIPTraceTypeEnum.LOG);
    subject = new CustomTracer(traceHistory, null, mock(CamelContext.class), sipTraceTypeEnumSet);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.dumpTrace(LOG_MESSAGE);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo(LOG_MESSAGE);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
    assertThat(traceHistory.getList()).isEmpty();
  }

  @Test
  void When_dumpTrace_With_MEMORY_Expect_messageInLog() {
    // arrange
    sipTraceTypeEnumSet.add(SIPTraceTypeEnum.MEMORY);
    subject = new CustomTracer(traceHistory, null, mock(CamelContext.class), sipTraceTypeEnumSet);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.dumpTrace(LOG_MESSAGE);

    // assert
    assertThat(logsList).isEmpty();
    assertThat(traceHistory.getAndClearHistory()).containsExactly(LOG_MESSAGE);
  }
}
