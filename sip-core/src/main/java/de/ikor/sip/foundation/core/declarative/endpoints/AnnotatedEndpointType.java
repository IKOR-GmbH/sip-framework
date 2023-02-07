package de.ikor.sip.foundation.core.declarative.endpoints;

/** Interface which provides {@link EndpointType} for each kind of {@link AnnotatedEndpoint}. */
public interface AnnotatedEndpointType {

  EndpointType getEndpointType();
}
