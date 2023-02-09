package de.ikor.sip.foundation.core.actuator.declarative.model;

import de.ikor.sip.foundation.core.declarative.annonations.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonations.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorType;
import lombok.*;

/**
 * Class which represents POJO model for exposing {@link InboundConnector} and {@link
 * OutboundConnector}
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointInfo {

  private String endpointId;
  private ConnectorType connectorType;
  private String camelEndpointUri; // TODO: Implement getting these information
  private String connectorId;
  private String scenarioId;
}
