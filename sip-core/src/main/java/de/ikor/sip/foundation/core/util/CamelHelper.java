package de.ikor.sip.foundation.core.util;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import org.apache.camel.Processor;
import org.apache.camel.processor.WrapProcessor;

/** Utility class to help with Camel's internals */
public class CamelHelper {

  private CamelHelper() {
    throw new SIPFrameworkException("Utility class");
  }

  /**
   * If an instance of WrapProcessor is received it will return the unwrapped Processor
   *
   * @param wrappedProcessor - Processor that can be wrapped by Camel in a WrapProcessor class
   * @return originalProcessor - Unwrapped processor
   */
  public static Processor unwrapProcessor(Processor wrappedProcessor) {
    Processor originalProcessor = wrappedProcessor;
    while (originalProcessor instanceof WrapProcessor) {
      originalProcessor = ((WrapProcessor) originalProcessor).getWrapped();
    }
    return originalProcessor;
  }
}
