package de.ikor.sip.foundation.core.framework;

import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.framework.emptyapp.EmptyTestingApplication;
import de.ikor.sip.foundation.core.framework.stubs.routers.ScopeBeanCentralRouter;
import de.ikor.sip.foundation.core.framework.stubs.ScopeAppendOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.ScopeBeanInConnector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(
    classes = {
      EmptyTestingApplication.class,
      ScopeBeanCentralRouter.class,
      ScopeBeanInConnector.class,
      ScopeAppendOutConnector.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class ConversationScopeTest {

  @Autowired TestRestTemplate testRestTemplate;

  @Test
  void When_sendGetRequestToRestConnector_Expect_ValidResponse() {
    ResponseEntity<String> response =
        testRestTemplate.getForEntity("/adapter/hello-bean", String.class);
    assertThat(response.getStatusCode()).describedAs(response.getBody()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("hello bean-bean");
  }
}
