package de.ikor.sip.foundation.core.framework.scope.conversation;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

@Slf4j
public class ConversationScope implements Scope {

  private static final String REFERENCE = "conversation";

  @Override
  public String getConversationId() {
    if (getScopeContext() == null) return "";
    String id = getScopeContext();
    log.debug("Scope Bound with Conversation from Exchange w/ scope id - {}", id);
    return id;
  }

  @Override
  public Object resolveContextualObject(String name) {
    return REFERENCE.equals(name);
  }

  @Override
  public Object get(String name, ObjectFactory<?> factory) {
    log.debug("Retrieving bean {}", name);
    Map<String, Object> beans = getOrCreateScopedBeans(getConversationId());
    return getScopedBean(beans, name, factory);
  }

  @Override
  public void registerDestructionCallback(String name, Runnable callback) {
    log.debug("Registering destruction callback to bean {}", name);
  }

  @Override
  public Object remove(String name) {
    return null;
  }

  protected String getScopeContext() {
    return ConversationContextHolder.instance().attributeHolder.get();
  }

  protected Map<String, Object> getOrCreateScopedBeans(String name) {
    Map<String, Map<String, Object>> scopeBeans =
        ConversationContextHolder.instance().getScopeBeans();
    return scopeBeans.computeIfAbsent(name, k -> new HashMap<>());
  }

  protected Object getScopedBean(Map<String, Object> map, String name, ObjectFactory<?> factory) {
    Object o = map.get(name);
    if (o == null) {
      o = factory.getObject();
      map.put(name, o);
    }
    return o;
  }
}
