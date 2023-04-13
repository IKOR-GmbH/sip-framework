package de.ikor.sip.foundation.core.actuator.declarative.model;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class which represents POJO model for exposing {@link InboundConnector} and {@link
 * OutboundConnector}
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectorInfo {

  private String connectorId;
  private ConnectorType connectorType;
  private String connectorDescription;
  private List<EndpointInfo> endpoints;
  private String connectorGroupId;
  private String scenarioId;
  private List<RouteInfo> routes;
  private String requestModelClass;

  private JsonSchema requestJsonForm;
  private String responseModelClass;

  private JsonSchema responseJsonForm;
}
