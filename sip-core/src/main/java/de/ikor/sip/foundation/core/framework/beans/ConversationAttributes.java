package de.ikor.sip.foundation.core.framework.beans;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConversationAttributes {
  private static final String DESTRUCTION_CB_PREFIX =
      ConversationAttributes.class.getName() + ".DESTRUCTION_CALLBACK.";
  private final Map<String, Runnable> destructionCallbacks = new HashMap<>();
  @Getter private final String conversationKey;

  @Getter protected final Map<String, Object> scope = new HashMap<>();

  /**
   * Create a new instance from an {@link String}
   *
   * @param key {@link String}
   */
  public ConversationAttributes(String key) {
    this.conversationKey = key;
  }


}
