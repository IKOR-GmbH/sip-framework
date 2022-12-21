package de.ikor.sip.foundation.core.framework.beans;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

@Slf4j
public class ConversationScope implements Scope {

  public static final String SCOPE_PROPERTY = "conversation";
  private static final String REFERENCE = "conversation";

  protected final ObjectFactory<?> mapFactory = HashMap::new;

  @Override
  public String getConversationId() {
    if (getConversationHelder() == null) return "";
    String id = getConversationHelder().getConversationId();
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
    Map<String, Object> beans =
        (Map<String, Object>) getScopedBean(getConversationHelder().getScope(), getConversationId(), mapFactory);
    return getScopedBean(beans, name, factory);
  }

  @Override
  public void registerDestructionCallback(String name, Runnable callback) {
    log.debug("Registering destruction callback to bean {}", name);
  }

  @Override
  public Object remove(String name) {
    log.debug("Removing bean {}", name);
    Map<String, Object> beans =
        (Map<String, Object>) getScopedBean(getConversationHelder().getScope(), getConversationId(), mapFactory);
    return beans.remove(name);
  }

  protected ConversationContextHolder getConversationHelder() {
    return ConversationContextHolder.instance();
  }

  protected Object getScopedBean(
      Map<String, Object> map, String name, ObjectFactory<?> factory) {
    Object o = map.get(name);
    if (o == null) {
      o = factory.getObject();
      map.put(name, o);
    }
    return o;
  }
}
