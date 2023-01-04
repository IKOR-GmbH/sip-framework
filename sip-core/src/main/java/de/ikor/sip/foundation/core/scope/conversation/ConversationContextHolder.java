package de.ikor.sip.foundation.core.scope.conversation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.apache.camel.Exchange;
import org.springframework.core.NamedThreadLocal;

public class ConversationContextHolder {
  private static final ConversationContextHolder instance = new ConversationContextHolder();
  public static final ThreadLocal<String> attributeHolder =
      new NamedThreadLocal<>("Conversation Context");

  @Getter protected final Map<String, Map<String, Object>> scopeBeans = new HashMap<>();
  private final Map<String, Set<String>> breadcrumbs = new HashMap<>();

  private ConversationContextHolder() {}

  public static ConversationContextHolder instance() {
    return instance;
  }

  public void setConversationAttributes(String key) {
    attributeHolder.set(key);
  }

  public void appendBreadcrumbs(String key, String exchangeId) {
    breadcrumbs.computeIfAbsent(key, k -> new HashSet<>());
    breadcrumbs.get(key).add(exchangeId);
  }

  public void removeBreadcrumbs(String key, Exchange exchange) {
    Set<String> crumbs = breadcrumbs.get(key);
    crumbs.remove(exchange.getExchangeId());
    if (crumbs.isEmpty()) {
      scopeBeans.remove(key);
    }
    attributeHolder.remove();
  }
}
