package de.ikor.sip.foundation.core.premiumsupport.registration;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

class AdapterRegistrationTest {
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private static ScheduledAnnotationBeanPostProcessor scheduledProcessor;

  @Mock private static SIPRegistrationClient registrationClient;
  private static AdapterRegistration subject;

  @BeforeAll
  public static void setUp() {
    registrationClient = mock(SIPRegistrationClient.class);
    scheduledProcessor = mock(ScheduledAnnotationBeanPostProcessor.class);
    subject = new AdapterRegistration(scheduledProcessor, registrationClient);
  }

  @Test
  void When_afterPropertiesSet_Expect_registerAdapterIsCalled() {
    // act
    subject.afterPropertiesSet();
    // assert
    verify(registrationClient, times(1)).registerAdapter();
  }

  @Test
  void When_destroy_Expect_unregisterAdapterIsCalled() {
    // act
    subject.destroy();
    // assert
    verify(registrationClient, times(1)).unregisterAdapter();
  }
}
