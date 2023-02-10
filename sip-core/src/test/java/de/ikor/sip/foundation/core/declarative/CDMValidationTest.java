package de.ikor.sip.foundation.core.declarative;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;
import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.declarative.CDMValidationAdapter;
import de.ikor.sip.foundation.core.apps.declarative.CDMValidationAdapter.CDMRequest;
import de.ikor.sip.foundation.core.apps.declarative.CDMValidationAdapter.CDMResponse;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.DisableJmx;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CamelSpringBootTest
@SpringBootTest(classes = {CDMValidationAdapter.class})
@DisableJmx(false)
@MockEndpoints("log:message*")
class CDMValidationTest {

  @EndpointInject("mock:log:message")
  private MockEndpoint mockedLogger;

  @Autowired private FluentProducerTemplate template;

  @BeforeEach
  void setup() {
    mockedLogger.reset();
  }

  @Test
  void when_CDMIsValid_then_NoExceptionOccurs() throws InterruptedException {
    CDMRequest cdmRequest = new CDMRequest(1000);
    CDMResponse cdmResponse = new CDMResponse("ID: 1000");
    mockedLogger.expectedMessageCount(1);
    mockedLogger.expectedBodiesReceived(cdmResponse);

    template.withBody(cdmRequest).to(direct("cdm-validator")).send();

    mockedLogger.assertIsSatisfied();
  }

  @Test
  void when_CDMRequestIsNotValid_then_ExceptionOccurs() {
    Exchange target = template.withBody("String").to(direct("cdm-validator")).send();

    assertThat(target.getException()).isInstanceOf(SIPFrameworkException.class);
  }

  //  @Test
  //  void when_CDMResponseIsNotValid_then_ExceptionOccurs() {
  //    Exchange target = template.withBody(new
  // CDMRequest(1001)).to(direct("cdm-validator")).send();
  //
  //    assertThat(target.getException()).isInstanceOf(SIPFrameworkException.class);
  //  }
}
