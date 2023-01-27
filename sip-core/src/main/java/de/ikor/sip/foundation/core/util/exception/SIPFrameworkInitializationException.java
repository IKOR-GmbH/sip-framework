package de.ikor.sip.foundation.core.util.exception;

/** Exception class for exception that are thrown by the framework during initialization phase */
public class SIPFrameworkInitializationException extends SIPFrameworkException{

    public SIPFrameworkInitializationException(String message) {
        super(message);
    }
}
