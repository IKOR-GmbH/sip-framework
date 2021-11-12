package de.ikor.sip.foundation.core.translate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class TranslationServiceDetailsOnStartupTest {

  TranslationServiceDetailsOnStartup translationServiceDetailsOnStartup;
  TranslateConfiguration translateConfiguration;
  List<String> fileLocations;
  ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void setup() {
    translateConfiguration = mock(TranslateConfiguration.class);
    when(translateConfiguration.getLang()).thenReturn("en");
    when(translateConfiguration.getDefaultEncoding()).thenReturn("UTF-8");
    translationServiceDetailsOnStartup =
        new TranslationServiceDetailsOnStartup(translateConfiguration);

    Logger logger =
        (Logger)
            LoggerFactory.getLogger(
                "de.ikor.sip.foundation.core.translate.TranslationServiceDetailsOnStartup");
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @Test
  void logTranslationDetails_noException() {
    List<ILoggingEvent> logsListSubject = listAppender.list;
    fileLocations = new ArrayList<>();
    fileLocations.add("classpath:sip-core-messages");
    when(translateConfiguration.getFileLocations()).thenReturn(fileLocations);

    translationServiceDetailsOnStartup.logTranslationDetails();

    assertThat(logsListSubject.get(3).getMessage()).isEqualTo("sip.core.translation.bundle_{}");
    assertThat(logsListSubject.get(3).getLevel()).isEqualTo(Level.INFO);
  }

  @Test
  void logTranslationDetails_withException() {
    List<ILoggingEvent> logsListSubject = listAppender.list;
    fileLocations = new ArrayList<>();
    fileLocations.add("classpath:sa");
    when(translateConfiguration.getFileLocations()).thenReturn(fileLocations);

    translationServiceDetailsOnStartup.logTranslationDetails();

    assertThat(logsListSubject.get(3).getMessage())
        .isEqualTo("sip.core.translation.missingbundle_{}");
    assertThat(logsListSubject.get(3).getLevel()).isEqualTo(Level.ERROR);
  }
}
