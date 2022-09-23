package de.ikor.sip.foundation.core.util;

import org.apache.camel.Processor;
import org.apache.camel.processor.WrapProcessor;

public class CamelHelper {

  public static Processor unwrapProcessor(Processor wrappedProcessor) {
    Processor originalProcessor = wrappedProcessor;
    while (originalProcessor instanceof WrapProcessor) {
      originalProcessor = ((WrapProcessor) originalProcessor).getWrapped();
    }
    return originalProcessor;
  }
}
