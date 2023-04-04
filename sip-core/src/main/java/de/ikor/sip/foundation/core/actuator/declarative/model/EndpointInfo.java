package de.ikor.sip.foundation.core.actuator.declarative.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Class which represents POJO model for exposing Camel endpoint with its route id. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointInfo {

  private String endpointId;
  private String camelEndpointUri;
  private Boolean primary;
}
