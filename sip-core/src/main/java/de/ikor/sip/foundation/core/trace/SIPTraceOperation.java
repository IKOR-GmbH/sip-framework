package de.ikor.sip.foundation.core.trace;

import java.util.function.BiConsumer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SIPTraceOperation {
  LOG("log", CustomTracer::logTrace),
  MEMORY("memory", CustomTracer::storeInMemory);

  public final String label;
  private final BiConsumer<CustomTracer, String> consumer;

  public void execute(CustomTracer customTracer, String out) {
    consumer.accept(customTracer, out);
  }
}
