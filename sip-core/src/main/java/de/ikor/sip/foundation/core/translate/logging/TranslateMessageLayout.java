package de.ikor.sip.foundation.core.translate.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Layout;
import de.ikor.sip.foundation.core.translate.SIPTranslateMessageService;
import org.mapstruct.factory.Mappers;

/**
 * Extends PatternLayout in order to keep logging pattern feature while trying to translate message
 * at the same time. In case that messageService bean is not available (ie. spring context is not
 * loaded yet) or that message key is not found, message will be logged untranslated.
 *
 * @param <E> event type
 */
@SuppressWarnings("java:S2326")
public class TranslateMessageLayout<E> extends PatternLayout implements Layout<ILoggingEvent> {
  private SIPTranslateMessageService messageService;

  @Override
  public String doLayout(ILoggingEvent event) {
    messageService = initMessageService();
    LoggingEvent cloneEvent = Mappers.getMapper(EventLogCloner.class).mapWithNoMessage(event);
    String translatedMessage;
    if (messageService == null) {
      translatedMessage = event.getMessage();
    } else {
      translatedMessage =
          messageService.getTranslatedMessage(event.getMessage(), event.getArgumentArray());
    }

    cloneEvent.setMessage(translatedMessage);
    return super.doLayout(cloneEvent);
  }

  private SIPTranslateMessageService initMessageService() {
    if (this.messageService == null) {
      this.messageService = SIPTranslateMessageService.get();
    }
    return this.messageService;
  }
}
