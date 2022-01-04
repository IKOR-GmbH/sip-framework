package de.ikor.sip.foundation.core.premiumsupport.registration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import static org.mockito.Mockito.*;

class AdapterRegistrationTest {
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private static ScheduledAnnotationBeanPostProcessor scheduledProcessor;
  @Mock private static SIPRegistrationWebClient sipRegistrationWebClient;
  private static AdapterRegistration subject;

  @BeforeAll
  public static void setUp() {
    sipRegistrationWebClient = mock(SIPRegistrationWebClient.class);
    scheduledProcessor = mock(ScheduledAnnotationBeanPostProcessor.class);
    subject = new AdapterRegistration(scheduledProcessor, sipRegistrationWebClient);
  }

  @Test
  void When_afterPropertiesSet_Expect_registerAdapterIsCalled() {
    // act
    subject.afterPropertiesSet();
    // assert
    verify(sipRegistrationWebClient, times(1)).registerAdapter();
  }

  @Test
  void When_destroy_Expect_unregisterAdapterIsCalled() {
    // act
    subject.destroy();
    // assert
    verify(sipRegistrationWebClient, times(1)).unregisterAdapter();
  }
}
