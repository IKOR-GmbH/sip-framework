package de.ikor.sip.foundation.core.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import de.ikor.sip.foundation.core.translate.SIPTranslateMessageService;
import de.ikor.sip.foundation.core.translate.logging.EventLogCloner;
import org.mapstruct.factory.Mappers;

/**
 * Attempts to translate the message through {@link SIPTranslateMessageService}. In case that
 * messageService bean is not available (ie. spring context is not * loaded yet) or that message key
 * is not found, message will remain untranslated.
 */
public class LoggingEventMessageEnricher {
  private static SIPTranslateMessageService messageService;

  private LoggingEventMessageEnricher() {}

  /**
   * Invokes message translation from SIPTranslateMessageService and sets the message in a new
   * LoggingEvent
   *
   * @param event {@link ILoggingEvent}
   * @return {@link LoggingEvent} with formatted message
   */
  public static LoggingEvent enrich(ILoggingEvent event) {
    messageService = SIPTranslateMessageService.get();
    LoggingEvent cloneEvent = Mappers.getMapper(EventLogCloner.class).mapWithNoMessage(event);
    String translatedMessage =
        messageService == null
            ? event.getMessage()
            : messageService.getTranslatedMessage(event.getMessage(), event.getArgumentArray());
    cloneEvent.setMessage(translatedMessage);
    return cloneEvent;
  }
}
