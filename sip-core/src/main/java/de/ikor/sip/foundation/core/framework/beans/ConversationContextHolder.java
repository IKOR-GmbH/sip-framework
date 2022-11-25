package de.ikor.sip.foundation.core.framework.beans;

import org.springframework.core.NamedThreadLocal;

public class ConversationContextHolder {
    private static final ConversationContextHolder instance = new ConversationContextHolder();
    private static final ThreadLocal<ConversationAttributes> attributeHolder = new NamedThreadLocal<>("Conversation Context");

    private ConversationContextHolder() {}


    public static ConversationContextHolder instance() {
        return instance;
    }

    public ConversationAttributes getConversationAttributes() throws IllegalStateException {
        return attributeHolder.get();
    }

    public void setConversationAttributes(ConversationAttributes context) {
        if(context == null) {
            resetConversationAttributes();
        } else {
            attributeHolder.set(context);
        }
    }

    public void resetConversationAttributes() {
        attributeHolder.remove();
    }

}
