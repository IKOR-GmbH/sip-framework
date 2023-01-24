package de.ikor.sip.foundation.core.declarative.endpoints;

import org.apache.camel.builder.EndpointConsumerBuilder;

public interface InboundEndpointDefinition extends InEndpointDefinition {

  EndpointConsumerBuilder getInboundEndpoint();
}
