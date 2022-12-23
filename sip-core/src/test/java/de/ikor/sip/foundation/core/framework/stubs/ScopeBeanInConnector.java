package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.scope.conversation.ConversationScopeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScopeBeanInConnector extends InConnector {
  @Autowired ConversationScopeBean conversationScopeBean;

  @Override
  public String getName() {
    return "bean-connector";
  }

  @Override
  public void configure() {
    from(rest("/hello-bean", "get-bean").get())
        .process(
            exchange -> {
              conversationScopeBean.setInternal("hello bean");
            });
  }

  @Override
  public void configureOnException() {}
}
