package de.ikor.sip.foundation.core.actuator.health.scheduler;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.util.ReflectionTestUtils;

class ScheduledHealthCheckTest {

  @Test
  void scheduledExecution() {
    // arrange
    ListAppender<ILoggingEvent> listAppender;
    Logger logger = (Logger) LoggerFactory.getLogger(ScheduledHealthCheck.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    HealthEndpoint endpoint = mock(HealthEndpoint.class);
    HealthComponent component = mock(HealthComponent.class);
    when(endpoint.health()).thenReturn(component);
    when(component.getStatus()).thenReturn(Status.UP);
    ScheduledHealthCheck scheduledHealthCheck = new ScheduledHealthCheck();
    ReflectionTestUtils.setField(scheduledHealthCheck, "healthEndpoint", endpoint);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    scheduledHealthCheck.scheduledExecution();

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo("sip.core.health.applicationstatus_{}");
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.INFO);
  }
}
