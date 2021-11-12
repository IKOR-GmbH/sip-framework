package de.ikor.sip.foundation.core.translate.logging;

import ch.qos.logback.core.Layout;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import org.slf4j.event.LoggingEvent;

/**
 * Extends PatternLayoutEncoderBase in order to enhance it with message translation feature by
 * featuring {@link TranslateMessageLayout} .
 *
 * @param <E> event type
 */
public class SIPPatternLayoutEncoder<E> extends PatternLayoutEncoderBase<E> {

  @Override
  public void start() {
    TranslateMessageLayout<LoggingEvent> patternLayout = new TranslateMessageLayout<>();
    patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
    patternLayout.setPattern(getPattern());
    patternLayout.setContext(context);
    patternLayout.start();
    this.layout = (Layout<E>) patternLayout;
    super.start();
  }
}
