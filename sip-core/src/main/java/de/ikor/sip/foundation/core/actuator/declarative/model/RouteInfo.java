package de.ikor.sip.foundation.core.actuator.declarative.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteInfo {

  private String routeId;
  private String routeRole;
}
