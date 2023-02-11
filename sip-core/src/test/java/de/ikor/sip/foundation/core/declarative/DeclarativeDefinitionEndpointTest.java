package de.ikor.sip.foundation.core.declarative;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorGroupInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.apps.declarative.SimpleAdapter;
import java.io.IOException;
import java.util.List;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.DisableJmx;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@CamelSpringBootTest
@SpringBootTest(
    classes = {SimpleAdapter.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisableJmx(false)
class DeclarativeDefinitionEndpointTest {

  @Autowired private ObjectMapper mapper;

  @LocalServerPort private int localServerPort;

  @Test
  void when_ActuatorGetScenarioInfo_then_RetrieveScenarios() throws IOException {
    // arrange
    HttpUriRequest request =
        new HttpGet(
            "http://localhost:" + localServerPort + "/actuator/adapterdefinition/scenarios");

    // act
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    CollectionType collectionType =
        mapper.getTypeFactory().constructCollectionType(List.class, IntegrationScenarioInfo.class);
    List<IntegrationScenarioInfo> scenarios =
        mapper.readValue(httpResponse.getEntity().getContent(), collectionType);

    // assert
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
    assertThat(scenarios)
        .anyMatch(
            scenarioInfo ->
                "Scenario used for testing which appends static message"
                    .equals(scenarioInfo.getScenarioDescription()))
        .hasSize(2);
  }

  @Test
  void when_ActuatorGetConnectorGroupInfo_then_RetrieveConnectors() throws IOException {
    // arrange
    HttpUriRequest request =
        new HttpGet(
            "http://localhost:" + localServerPort + "/actuator/adapterdefinition/connectorgroups");

    // act
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    CollectionType collectionType =
        mapper.getTypeFactory().constructCollectionType(List.class, ConnectorGroupInfo.class);
    List<ConnectorGroupInfo> connectorGroups =
        mapper.readValue(httpResponse.getEntity().getContent(), collectionType);

    // assert
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
    assertThat(connectorGroups)
        .anyMatch(
            connectorGroupInfo ->
                "Test Documentation for a connector group"
                    .equals(connectorGroupInfo.getConnectorGroupDescription()))
        .anyMatch(
            connectorGroupInfo ->
                "Test Documentation for an implicitly created connectorgroup"
                    .equals(connectorGroupInfo.getConnectorGroupDescription()))
        .hasSize(2);
  }

  @Test
  void when_ActuatorGetConnectorInfo_then_RetrieveEndpoints() throws IOException {
    // arrange
    HttpUriRequest request =
        new HttpGet(
            "http://localhost:" + localServerPort + "/actuator/adapterdefinition/connectors");

    // act
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    CollectionType collectionType =
        mapper.getTypeFactory().constructCollectionType(List.class, ConnectorInfo.class);
    List<ConnectorInfo> connectors =
        mapper.readValue(httpResponse.getEntity().getContent(), collectionType);

    // assert
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
    assertThat(connectors)
        .anyMatch(
            connectorInfo ->
                "Provides messages from direct to AppendStaticMessage scenario"
                    .equals(connectorInfo.getConnectorDescription()))
        .hasSize(4);
  }
}
