package de.ikor.sip.foundation.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SIPStaticSpringContext.class)
class SIPStaticSpringContextIntegrationTest {

  @Autowired private ApplicationContext context;
  @Autowired private SIPStaticSpringContext subject;

  @Test
  void When_StaticContextIsInjected_Expect_DefaultApplicationContext() {
    // assert
    assertThat(ReflectionTestUtils.getField(subject, "context")).isEqualTo(context);
  }
}
