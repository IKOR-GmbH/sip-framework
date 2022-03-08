package de.ikor.sip.testkit.exception.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class ExceptionLoggerTest {

  ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    Logger logger = (Logger) LoggerFactory.getLogger(ExceptionLogger.class);
    listAppender = new ListAppender<>();
    listAppender.start();

    logger.addAppender(listAppender);
  }

  @Test
  void logException() {
    ExceptionLogger.logException(mock(Exception.class));
    List<ILoggingEvent> logsList = listAppender.list;

    assertEquals("Exception: {}", logsList.get(0).getMessage());
    assertEquals(Level.ERROR, logsList.get(0).getLevel());

    assertEquals("Message: {}", logsList.get(1).getMessage());
    assertEquals(Level.ERROR, logsList.get(1).getLevel());
  }

  @Test
  void logTestCaseException() {
    ExceptionLogger.logTestCaseException(mock(Exception.class), "testname");
    List<ILoggingEvent> logsList = listAppender.list;

    assertEquals(
        "Error occurred during initialization of test case: {}", logsList.get(0).getMessage());
    assertEquals(Level.ERROR, logsList.get(0).getLevel());

    assertEquals("Exception: {}", logsList.get(1).getMessage());
    assertEquals(Level.ERROR, logsList.get(1).getLevel());

    assertEquals("Message: {}", logsList.get(2).getMessage());
    assertEquals(Level.ERROR, logsList.get(2).getLevel());
  }
}
