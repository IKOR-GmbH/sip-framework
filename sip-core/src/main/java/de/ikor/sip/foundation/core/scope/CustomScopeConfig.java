package de.ikor.sip.foundation.core.scope;

import de.ikor.sip.foundation.core.scope.conversation.ConversationScope;
import de.ikor.sip.foundation.core.scope.exchange.ExchangeScope;
import java.util.HashMap;
import java.util.Map;
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
