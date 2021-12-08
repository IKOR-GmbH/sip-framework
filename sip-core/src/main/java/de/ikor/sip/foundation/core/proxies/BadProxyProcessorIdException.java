package de.ikor.sip.foundation.core.proxies;

/** Exception for bad processor id when registering mock function */
public class BadProxyProcessorIdException extends RuntimeException {

    /**
     * Creates new instance of BadProxyProcessorIdException
     *
     * @param message message to be shown
     */
    public BadProxyProcessorIdException(String message) { super(message); }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
