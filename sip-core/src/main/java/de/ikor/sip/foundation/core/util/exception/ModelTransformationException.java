package de.ikor.sip.foundation.core.util.exception;

/** Exception which occurs when the model transformation was unsuccessful */
public class ModelTransformationException extends SIPAdapterException {
  public ModelTransformationException() {}

  public ModelTransformationException(String message) {
    super(message);
  }
}
