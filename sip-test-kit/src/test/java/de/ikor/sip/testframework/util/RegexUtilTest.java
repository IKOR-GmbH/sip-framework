package de.ikor.sip.testframework.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RegexUtilTest {
  RegexUtil regexUtil;

  @Test
  void compare_Success() {
    assertThat(RegexUtil.compare("sth.sth", "sthasth")).isTrue();
  }

  @Test
  void compare_Fail() {
    assertThat(RegexUtil.compare("sth.sth", "sthsth")).isFalse();
  }
}
