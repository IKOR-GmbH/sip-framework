package de.ikor.sip.foundation.core.scope;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.scope.conversation.ConversationScopeBean;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SIPIntegrationAdapter
public class SimpleScopedApplication extends RouteBuilder {

  @Autowired ConversationScopeBean conversationScopeBean;

  @Override
  public void configure() throws Exception {
    rest().get("/hello-bean").to("direct:get-bean");
    from("direct:get-bean")
        .process(
            exchange -> {
              conversationScopeBean.setInternal("hello bean");
            })
        .to("seda:hello-bean");

    from("seda:hello-bean")
        .process(
            exchange -> {
              String body = conversationScopeBean.getInternal() + "-bean";
              exchange.getMessage().setBody(body);
            });
  }
}
