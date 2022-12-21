package de.ikor.sip.foundation.core.framework.beans;

import lombok.Getter;
import org.springframework.core.NamedThreadLocal;

import java.util.HashMap;
import java.util.Map;

public class ConversationContextHolder {
  private static final ConversationContextHolder instance = new ConversationContextHolder();
  private static final ThreadLocal<String> attributeHolder =
      new NamedThreadLocal<>("Conversation Context");

  @Getter protected final Map<String, Object> scope = new HashMap<>();

  private ConversationContextHolder() {}

  public static ConversationContextHolder instance() {
    return instance;
  }

  public String getConversationId() throws IllegalStateException {
    return attributeHolder.get();
  }

  public void setConversationAttributes(String breadcrumbId) {
    if (breadcrumbId == null) {
      resetConversationAttributes();
    } else {
      attributeHolder.set(breadcrumbId);
    }
  }

  public void resetConversationAttributes() {
    attributeHolder.remove();
  }
}
