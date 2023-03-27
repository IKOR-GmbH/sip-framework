package de.ikor.sip.foundation.core.actuator.declarative.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean inputEndpoint = null;
}
