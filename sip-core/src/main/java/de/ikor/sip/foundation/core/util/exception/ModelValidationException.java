package de.ikor.sip.foundation.core.util.exception;

/** Exception which occurs when the model is invalid */
public class ModelValidationException extends SIPAdapterException {
  public ModelValidationException() {}

  public ModelValidationException(String message) {
    super(message);
  }
}
