package de.ikor.sip.foundation.core.framework.beans;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConversationScopeConfig {
  @Bean
  public CustomScopeConfigurer customScope() {
    CustomScopeConfigurer configurer = new CustomScopeConfigurer();
    Map<String, Object> workflowScope = new HashMap<>();
    workflowScope.put("conversation", new ConversationScope());
    configurer.setScopes(workflowScope);

    return configurer;
  }
}
