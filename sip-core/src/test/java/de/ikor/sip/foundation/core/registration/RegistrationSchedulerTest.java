package de.ikor.sip.foundation.core.registration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RegistrationSchedulerTest {

  @ParameterizedTest
  @ValueSource(longs = {1000L, 5000L, 100000L, 120000L})
  void test_If_Valid_Intervals_Are_Causing_No_Problems(Long fixedRate) {
    assertDoesNotThrow(() -> new RegistrationScheduler(fixedRate));
  }

  @ParameterizedTest
  @ValueSource(longs = {-1000L, 0L, 900L, 130000L})
  void test_If_Invalid_Intervals_Are_Causing_Problems(Long fixedRate) {
    assertThrows(IllegalArgumentException.class, () -> new RegistrationScheduler(fixedRate));
  }
}
