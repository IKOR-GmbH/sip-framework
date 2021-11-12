package de.ikor.sip.foundation.core.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

class SIPStaticSpringContextTest {

  ApplicationContext applicationContext;
  SIPStaticSpringContext sipStaticSpringContext;

  @BeforeEach
  void setUp() {
    applicationContext = mock(ApplicationContext.class);
    sipStaticSpringContext = new SIPStaticSpringContext();
  }

  @Test
  @DisplayName(
      "Given any bean from context, when setting field context, then this and context bean shall be equal")
  void getBean() {
    // arrange
    Object bean = new Object();
    when(applicationContext.getBean(any(Class.class))).thenReturn(bean);

    // act
    ReflectionTestUtils.setField(sipStaticSpringContext, "context", applicationContext);

    // assert
    assertThat(bean).isEqualTo(SIPStaticSpringContext.getBean((Object.class)));
  }

  @Test
  @DisplayName(
      "Given any bean from context, when setting field context, then this and context bean shall be equal")
  void getBean_ExceptionThrown() {
    // arrange
    when(applicationContext.getBean(any(Class.class))).thenThrow(new RuntimeException());

    // act
    ReflectionTestUtils.setField(sipStaticSpringContext, "context", applicationContext);

    // assert
    assertThat(SIPStaticSpringContext.getBean(Object.class)).isNull();
  }

  @Test
  void setApplicationContext() {
    // arrange
    sipStaticSpringContext.setApplicationContext(applicationContext);

    // assert
    assertThat(applicationContext)
        .isEqualTo(ReflectionTestUtils.getField(sipStaticSpringContext, "context"));
  }
}
