package de.ikor.sip.foundation.core.framework.beans;

import org.springframework.beans.factory.config.Scope;

public class DefaultScopeDestructionCallback implements Runnable {

    private final Scope scope;
    private final String name;

    public DefaultScopeDestructionCallback(Scope scope, String name) {
        this.scope = scope;
        this.name = name;
    }

    /**
     * Removes the bean from the scope
     */
    public void run() {
        scope.remove(name);
    }

}
