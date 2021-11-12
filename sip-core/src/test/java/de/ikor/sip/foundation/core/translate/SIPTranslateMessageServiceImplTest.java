package de.ikor.sip.foundation.core.translate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

class SIPTranslateMessageServiceImplTest {

  SIPTranslateMessageServiceImpl sipTranslateMessageService;
  MessageSource messageSource;
  TranslateConfiguration translateConfiguration;
  private static final String MESSAGE_KEY = "key";
  private static final String MESSAGE_CONTENT = "test";

  @BeforeEach
  void setUp() {
    messageSource = mock(MessageSource.class);
    translateConfiguration = mock(TranslateConfiguration.class);
    when(translateConfiguration.getLang()).thenReturn("en");
    sipTranslateMessageService =
        new SIPTranslateMessageServiceImpl(messageSource, translateConfiguration);
  }

  @Test
  void getTranslatedMessage_noArguments() {
    // arrange
    when(messageSource.getMessage(MESSAGE_KEY, null, new Locale("en"))).thenReturn(MESSAGE_CONTENT);

    // assert
    assertThat(sipTranslateMessageService.getTranslatedMessage(MESSAGE_KEY))
        .isEqualTo(MESSAGE_CONTENT);
  }

  @Test
  void testGetTranslatedMessage_withArguments() {
    // arrange
    Object[] array = new Object[1];
    when(messageSource.getMessage(MESSAGE_KEY, array, new Locale("en")))
        .thenReturn(MESSAGE_CONTENT);

    assertThat(sipTranslateMessageService.getTranslatedMessage(MESSAGE_KEY, array))
        .isEqualTo(MESSAGE_CONTENT);
  }
}
