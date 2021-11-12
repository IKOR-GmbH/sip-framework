package de.ikor.sip.foundation.core.translate.logging;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SIPPatternLayoutEncoderTest {

  @Test
  void start_successful() {
    // arrange
    SIPPatternLayoutEncoder sipPatternLayoutEncoder = new SIPPatternLayoutEncoder();

    // act
    sipPatternLayoutEncoder.start();

    // assign
    assertThat((Boolean) ReflectionTestUtils.getField(sipPatternLayoutEncoder, "started")).isTrue();
  }
}
