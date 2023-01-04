package de.ikor.sip.foundation.core.scope;

import de.ikor.sip.foundation.core.scope.conversation.ConversationScope;
import java.util.HashMap;
import java.util.Map;

import de.ikor.sip.foundation.core.scope.exchange.ExchangeScope;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
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
