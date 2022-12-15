package de.ikor.sip.foundation.core.framework.beans;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

@Slf4j
public class ConversationAttributes {
  private static final String DESTRUCTION_CB_PREFIX =
      ConversationAttributes.class.getName() + ".DESTRUCTION_CALLBACK.";
  private final Map<String, Runnable> destructionCallbacks = new HashMap<>();
  private final Exchange exchange;

  /**
   * Create a new instance from an {@link Exchange}
   *
   * @param exchange {@link Exchange}
   */
  public ConversationAttributes(Exchange exchange) {
    this.exchange = exchange;
  }

  public Object get() {
    return exchange;
  }

  public void registerDestructionCallback(String name, Runnable callback) {
    synchronized (this.destructionCallbacks) {
      destructionCallbacks.put(DESTRUCTION_CB_PREFIX + name, callback);
    }
  }

  public void executeDestructionCallbacks() {
    synchronized (this.destructionCallbacks) {
      for (Map.Entry<String, Runnable> cb : destructionCallbacks.entrySet()) {
        log.debug("Executing destruction callback: " + cb.getKey());
        cb.getValue().run();
      }
      destructionCallbacks.clear();
    }
  }
}
