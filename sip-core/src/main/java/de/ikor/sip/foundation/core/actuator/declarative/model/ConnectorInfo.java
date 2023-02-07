package de.ikor.sip.foundation.core.actuator.declarative.model;

import de.ikor.sip.foundation.core.declarative.annonations.Connector;
import java.util.List;
import lombok.*;

/** Class which represents POJO model for exposing {@link Connector} */
@Value
@Builder
public class ConnectorInfo {

  String connectorId;
  String connectorDescription;
  List<String> inboundEndpoints;
  List<String> outboundEndpoints;
}
