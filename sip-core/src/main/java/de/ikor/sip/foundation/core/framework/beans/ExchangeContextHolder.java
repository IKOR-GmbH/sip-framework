package de.ikor.sip.foundation.core.framework.beans;

import org.springframework.core.NamedThreadLocal;

public class ExchangeContextHolder {
    private static final ExchangeContextHolder instance = new ExchangeContextHolder();
    private static final ThreadLocal<ExchangeAttributes> attributeHolder = new NamedThreadLocal<>("Exchange Context");

    private ExchangeContextHolder() {}


    public static ExchangeContextHolder instance() {
        return instance;
    }

    public ExchangeAttributes getContext() throws IllegalStateException {
        ExchangeAttributes attributes = attributeHolder.get();

        if(attributes == null) {
            throw new IllegalStateException("No thread-bound exchange found");
        }

        return attributes;
    }

    public void setContext(ExchangeAttributes context) {
        if(context == null) {
            resetContext();
        } else {
            attributeHolder.set(context);
        }
    }

    public void resetContext() {
        attributeHolder.remove();
    }

}
