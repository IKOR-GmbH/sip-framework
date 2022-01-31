package de.ikor.sip.foundation.core.trace;

import lombok.AllArgsConstructor;

import java.util.function.BiConsumer;

@AllArgsConstructor
public enum SIPTraceTypeEnum {
  LOG("log", CustomTracer::logTrace),
  MEMORY("memory", CustomTracer::storeInMemory);

  public final String label;
  private final BiConsumer<CustomTracer, String> consumer;

  public void execute(CustomTracer customTracer, String out) {
    consumer.accept(customTracer, out);
  }

}
