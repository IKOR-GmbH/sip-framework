package de.ikor.sip.foundation.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    subject.setApplicationContext(applicationContext);

    // assert
    assertThat(SIPStaticSpringContext.getBean(Object.class)).isNull();
  }

  @Test
  void When_setApplicationContext_Expect_ApplicationContextSet() {
    // act
    subject.setApplicationContext(applicationContext);

    // assert
    assertThat(ReflectionTestUtils.getField(subject, "context")).isEqualTo(applicationContext);
  }
}
