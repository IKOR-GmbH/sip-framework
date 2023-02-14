package de.ikor.sip.foundation.core.declarative;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoutesRegistryTest {

  private RoutesRegistry subject;
  private DeclarationRegistryApi declarationRegistryApi;

  @BeforeEach
  void setup() {
    declarationRegistryApi = mock(DeclarationRegistryApi.class);
    subject = new RoutesRegistry(declarationRegistryApi, null);
  }

  @Test
  void when_getRouteIdByConnectorId_then_validateConnectorsRouteId() {
    // arrange
    ConnectorDefinition connectorDefinitionMock = mock(ConnectorDefinition.class);
    when(declarationRegistryApi.getConnectorById("connectorId"))
        .thenReturn(Optional.of(connectorDefinitionMock));
    when(connectorDefinitionMock.getId()).thenReturn("connectorId");
    subject.generateRouteIdForConnector(RouteRole.EXTERNAL_ENDPOINT, connectorDefinitionMock);

    // act
    String actual = subject.getRouteIdByConnectorId("connectorId");

    // assert
    assertThat(actual).isEqualTo("sip-connector_connectorId_externalEndpoint");
  }
}
