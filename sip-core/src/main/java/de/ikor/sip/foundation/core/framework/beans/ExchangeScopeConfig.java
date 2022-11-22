package de.ikor.sip.foundation.core.framework.beans;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ExchangeScopeConfig {
    @Bean
    public CustomScopeConfigurer customScope () {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer ();
        Map<String, Object> workflowScope = new HashMap<>();
        workflowScope.put("exchange", new ExchangeScope());
        configurer.setScopes(workflowScope);

        return configurer;
    }
}
