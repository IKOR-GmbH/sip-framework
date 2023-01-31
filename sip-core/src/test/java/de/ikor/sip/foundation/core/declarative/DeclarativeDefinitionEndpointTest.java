package de.ikor.sip.foundation.core.declarative;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import de.ikor.sip.foundation.core.actuator.declarative.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.EndpointInfo;
import de.ikor.sip.foundation.core.actuator.declarative.IntegrationScenarioInfo;
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
    assertThat(scenarios.size()).isEqualTo(2);
  }

  @Test
  void when_ActuatorGetConnectorInfo_then_RetrieveConnectors() throws IOException {
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
    assertThat(connectors.size()).isEqualTo(2);
  }

  @Test
  void when_ActuatorGetEndpointInfo_then_RetrieveEndpoints() throws IOException {
    // arrange
    HttpUriRequest request =
        new HttpGet(
            "http://localhost:" + localServerPort + "/actuator/adapterdefinition/endpoints");

    // act
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
    CollectionType collectionType =
        mapper.getTypeFactory().constructCollectionType(List.class, EndpointInfo.class);
    List<EndpointInfo> endpoints =
        mapper.readValue(httpResponse.getEntity().getContent(), collectionType);

    // assert
    assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(200);
    assertThat(endpoints.size()).isEqualTo(4);
  }
}
