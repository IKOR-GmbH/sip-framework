package de.ikor.sip.foundation.core.scope.conversation;

import org.apache.camel.Exchange;
import org.apache.camel.support.EventHelper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.ikor.sip.foundation.core.scope.conversation.notifiers.ConversationCreatedExchangeEventNotifier.SCOPE_PROPERTY;

@Component
public class ConversationTracker {
  private final Map<String, Set<String>> breadcrumbs = new HashMap<>();

  public void registerOpenedExchange(String key, String exchangeId) {
    breadcrumbs.computeIfAbsent(key, k -> new HashSet<>());
    breadcrumbs.get(key).add(exchangeId);
  }

  public void registerClosedExchange(Exchange exchange) {
    String key = exchange.getProperty(SCOPE_PROPERTY, String.class);
    Set<String> crumbs = breadcrumbs.get(key);
    crumbs.remove(exchange.getExchangeId());
    if (crumbs.isEmpty()) {
      sendConversationCompletedEvent(exchange);
    }
  }

  private void sendConversationCompletedEvent(Exchange exchange) {
    exchange.setProperty("finalExchangeOnRoute", true);
    EventHelper.notifyExchangeDone(exchange.getContext(), exchange);
  }
}
