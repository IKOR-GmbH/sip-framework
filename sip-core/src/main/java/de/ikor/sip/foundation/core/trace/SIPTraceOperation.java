package de.ikor.sip.foundation.core.trace;

import lombok.AllArgsConstructor;
import org.apache.camel.util.function.TriConsumer;

@AllArgsConstructor
public enum SIPTraceOperation {
  LOG("log", CustomTracer::logTrace),
  MEMORY("memory", CustomTracer::storeInMemory);

  public final String label;
  private final TriConsumer<CustomTracer, String, Object> consumer;

  public void execute(CustomTracer customTracer, String out, Object node) {
    consumer.accept(customTracer, out, node);
  }
}
