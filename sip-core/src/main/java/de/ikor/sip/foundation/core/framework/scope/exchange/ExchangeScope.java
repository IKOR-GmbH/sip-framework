package de.ikor.sip.foundation.core.framework.scope.exchange;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

@Slf4j
public class ExchangeScope implements Scope {

  private static final String REFERENCE = "exchange";

  @Override
  public String getConversationId() {
    return "";
  }

  @Override
  public Object resolveContextualObject(String name) {
    return REFERENCE.equals(name);
  }

  @Override
  public Object get(String name, ObjectFactory<?> factory) {
    log.debug("Retrieving bean {}", name);
    return getOrCreateScopedBean(name, factory);
  }

  @Override
  public void registerDestructionCallback(String name, Runnable callback) {
    // not used
  }

  @Override
  public Object remove(String name) {
    ExchangeContextHolder.instance().resetConversationAttributes();
    return null;
  }

  protected Object getOrCreateScopedBean(String name, ObjectFactory<?> factory) {

    ExchangeAttributes conversationAttributes = ExchangeContextHolder.attributeHolder.get();
    Object o = conversationAttributes.getScopeBeans().get(name);
    if (o == null) {
      o = factory.getObject();
      conversationAttributes.putBeanInScope(name, o);
    }
    return o;
  }
}
