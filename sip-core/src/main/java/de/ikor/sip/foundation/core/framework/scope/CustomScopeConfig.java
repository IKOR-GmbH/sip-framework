package de.ikor.sip.foundation.core.framework.scope;

import de.ikor.sip.foundation.core.framework.scope.conversation.ConversationScope;
import java.util.HashMap;
import java.util.Map;

import de.ikor.sip.foundation.core.framework.scope.exchange.ExchangeScope;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomScopeConfig {
  @Bean
  public CustomScopeConfigurer customScope() {
    CustomScopeConfigurer configurer = new CustomScopeConfigurer();
    Map<String, Object> workflowScope = new HashMap<>();
    workflowScope.put("exchange", new ExchangeScope());
    workflowScope.put("conversation", new ConversationScope());
    configurer.setScopes(workflowScope);

    return configurer;
  }
}
