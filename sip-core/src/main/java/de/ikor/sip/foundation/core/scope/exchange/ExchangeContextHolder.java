package de.ikor.sip.foundation.core.scope.exchange;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.apache.camel.Exchange;
import org.springframework.core.NamedThreadLocal;

public class ExchangeContextHolder {
    private static final ExchangeContextHolder instance = new ExchangeContextHolder();
    public static final ThreadLocal<ExchangeAttributes> attributeHolder =
            new NamedThreadLocal<>("Conversation Context");

    @Getter protected final Map<String, Object> scope = new HashMap<>();

    private ExchangeContextHolder() {}

    public static ExchangeContextHolder instance() {
        return instance;
    }

    public void setConversationAttributes(Exchange exchange) {
        if (exchange == null) {
            resetConversationAttributes();
        } else {
            attributeHolder.set(new ExchangeAttributes(exchange));
        }
    }

    public void resetConversationAttributes() {
        attributeHolder.remove();
    }
}
