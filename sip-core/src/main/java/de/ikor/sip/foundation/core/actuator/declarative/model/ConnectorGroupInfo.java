package de.ikor.sip.foundation.core.actuator.declarative.model;

import de.ikor.sip.foundation.core.declarative.annonation.ConnectorGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/** Class which represents POJO model for exposing {@link ConnectorGroup} */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectorGroupInfo {

  private String connectorGroupId;
  private String connectorGroupDescription;
  private List<String> inboundConnectors;
  private List<String> outboundConnectors;
}
