package de.ikor.sip.foundation.core.translate.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Layout;
import de.ikor.sip.foundation.core.util.LoggingEventMessageEnricher;

/**
 * Extends PatternLayout in order to keep logging pattern feature while trying to translate message
 * at the same time.
 *
 * @param <E> event type
 */
@SuppressWarnings("java:S2326")
public class TranslateMessageLayout<E> extends PatternLayout implements Layout<ILoggingEvent> {

  @Override
  public String doLayout(ILoggingEvent event) {
    LoggingEvent cloneEvent = LoggingEventMessageEnricher.enrich(event);
    cloneEvent.setLevel(event.getLevel());
    return super.doLayout(cloneEvent);
  }
}
