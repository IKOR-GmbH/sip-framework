package de.ikor.sip.foundation.core.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

class SIPStaticSpringContextTest {

  private ApplicationContext applicationContext;
  private SIPStaticSpringContext subject;

  @BeforeEach
  void setUp() {
    applicationContext = mock(ApplicationContext.class);
    subject = new SIPStaticSpringContext();
  }

  @Test
  @DisplayName(
      "Given any bean from context, when setting field context, then this and context bean shall be equal")
  void When_getBeanFromStaticContext_Expect_BeanReturned() {
    // arrange
    Object bean = new Object();
    when(applicationContext.getBean(any(Class.class))).thenReturn(bean);
    subject.setApplicationContext(applicationContext);

    // assert
    assertThat(SIPStaticSpringContext.getBean((Object.class))).isEqualTo(bean);
  }

  @Test
  void When_getNonExistingBean_Expect_nullBean() {
    // arrange
    when(applicationContext.getBean(any(Class.class))).thenThrow(new RuntimeException());
    Logger logger =
        (Logger) LoggerFactory.getLogger("de.ikor.sip.foundation.core.util.SIPStaticSpringContext");
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    List<ILoggingEvent> logsList = listAppender.list;
    subject.setApplicationContext(applicationContext);

    // assert
    assertThat(SIPStaticSpringContext.getBean(Object.class)).isNull();
    assertThat(logsList.get(0).getMessage()).isEqualTo("sip.core.util.missingbean_{}");
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.DEBUG);
  }

  @Test
  void When_setApplicationContext_Expect_ApplicationContextSet() {
    // act
    subject.setApplicationContext(applicationContext);

    // assert
    assertThat(ReflectionTestUtils.getField(subject, "context"))
            .isEqualTo(applicationContext);
  }
}
