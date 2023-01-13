package de.ikor.sip.foundation.core.scope.conversation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

@Slf4j
@RequiredArgsConstructor
public class ConversationScope implements Scope {

  private static final String REFERENCE = "conversation";
  private final ConversationContextHolder conversationHolder;

  @Override
  public String getConversationId() {
    return null; // implementation not needed
  }

  @Override
  public Object resolveContextualObject(String name) {
    return REFERENCE.equals(name);
  }

  @Override
  public Object get(String name, ObjectFactory<?> factory) {
    log.debug("Retrieving bean {}", name);
    return conversationHolder.getOrCreateScopedBean(name, factory);
  }

  @Override
  public void registerDestructionCallback(String name, Runnable callback) {}

  @Override
  public Object remove(String name) {
    return null;
  }
}
