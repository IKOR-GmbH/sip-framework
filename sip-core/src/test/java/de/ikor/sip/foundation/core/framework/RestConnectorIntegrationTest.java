package de.ikor.sip.foundation.core.framework;

import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.framework.CentralRouterTestingApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
    classes = {CentralRouterTestingApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestConnectorIntegrationTest {
  @Autowired TestRestTemplate testRestTemplate;

  @Test
  void When_sendGetRequestToRestConnector_Expect_ValidResponse() {
    ResponseEntity<String> response =
        testRestTemplate.getForEntity("/adapter/hello-append", String.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("hello rest-append");
  }
}
