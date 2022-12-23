package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.scope.conversation.ConversationScopeBean;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScopeAppendOutConnector extends OutConnector {
  @Autowired ConversationScopeBean conversationScopeBean;

  @Override
  public void configure(RouteDefinition route) {
    route.process(
        exchange -> {
          String body = conversationScopeBean.getInternal() + "-bean";
          exchange.getMessage().setBody(body);
        });
  }
}
