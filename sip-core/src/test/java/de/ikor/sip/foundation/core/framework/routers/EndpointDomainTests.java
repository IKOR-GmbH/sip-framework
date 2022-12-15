package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.apps.framework.emptyapp.EmptyTestingApplication;
import de.ikor.sip.foundation.core.framework.stubs.routers.TestingCentralRouter;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;
import de.ikor.sip.foundation.core.framework.stubs.*;
import de.ikor.sip.foundation.core.framework.testutil.TestSetupUtil;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.DisableJmx;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

@CamelSpringBootTest
@SpringBootTest(classes = EmptyTestingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@MockEndpoints("log:message*")
@DisableJmx(false)
class EndpointDomainTests {
  private static final String DIRECT_IN_ID = "inBasicConnector";

  private final TestingCentralRouter subject = new TestingCentralRouter();

  private String inEndpointUri;

  @Autowired private ProducerTemplate template;

  @EndpointInject("mock:log:messageIn")
  private MockEndpoint mockInEndpoint;

  @EndpointInject("mock:log:messageOut")
  private MockEndpoint mockOutEndpoint;

  @BeforeEach
  void setup() {
    mockInEndpoint.reset();
    mockOutEndpoint.reset();
    inEndpointUri = TestSetupUtil.getNextEndpointId();
  }

  @Test
  void GIVEN_inEndpointDomain_WHEN_sendingInEndpointDomainAsPayload_THEN_successfulValidation()
      throws Exception {
    // arrange
    InConnector inConnector =
         SimpleInConnector.withEndpoint(InEndpoint.instance(inEndpointUri, DIRECT_IN_ID, InEndpointDomain.class));
    InEndpointDomain inDomain = new InEndpointDomain("hello world");
    subject.input(inConnector).sequencedOutput(new SimpleOutConnector());
    subject.toCentralRouter().setUpRoutes();

    mockInEndpoint.expectedBodiesReceived(inDomain);
    mockInEndpoint.expectedMessageCount(1);

    // act
    template.sendBody(inEndpointUri, inDomain);

    // assert
    mockInEndpoint.assertIsSatisfied();
  }

  @Test
  void
      GIVEN_inEndpointDomainAndTransformation_WHEN_sendingRawStringAsPayload_THEN_successfulTransformationAndValidation()
          throws Exception {
    // arrange
    InConnector inConnector =
            SimpleInConnector.withEndpoint(InEndpoint.instance(
                    inEndpointUri, DIRECT_IN_ID, InEndpointDomain.class, InEndpointDomain::new));

    InEndpointDomain expected = new InEndpointDomain("hello world");
    subject.input(inConnector).sequencedOutput(new SimpleOutConnector());
    subject.toCentralRouter().setUpRoutes();

    mockInEndpoint.message(0).body(InEndpointDomain.class).contains(expected);
    mockInEndpoint.expectedMessageCount(1);

    // act
    template.sendBody(inEndpointUri, "hello world");

    // assert
    mockInEndpoint.assertIsSatisfied();
  }

  @Test
  void GIVEN_inEndpointDomain_WHEN_sendingRawStringAsPayload_THEN_unsuccessfulValidation() {
    // arrange
    InConnector inConnector =
        SimpleInConnector.withEndpoint(InEndpoint.instance(inEndpointUri, DIRECT_IN_ID, InEndpointDomain.class));
    subject.input(inConnector).sequencedOutput(new SimpleOutConnector());
    subject.toCentralRouter().setUpRoutes();

    // act & assert
    assertThrows(
        CamelExecutionException.class, () -> template.sendBody(inEndpointUri, "string type value"));
  }

  @Test
  void GIVEN_outEndpointDomain_WHEN_sendingOutEndpointDomainAsPayload_THEN_successfulValidation()
      throws Exception {
    // arrange
    InConnector inConnector = SimpleInConnector.withEndpoint(InEndpoint.instance(inEndpointUri, DIRECT_IN_ID));
    OutConnectorStub outConnector =
        new OutConnectorStub(
            OutEndpoint.instance("log:foo", "outBasicConnector", OutEndpointDomain.class));
    OutEndpointDomain outEndpointDomain = new OutEndpointDomain("hello world");
    subject.input(inConnector).sequencedOutput(outConnector);
    subject.toCentralRouter().setUpRoutes();

    mockOutEndpoint.expectedBodiesReceived(outEndpointDomain);
    mockOutEndpoint.expectedMessageCount(1);

    // act
    template.sendBody("direct:OutConnectorStub", outEndpointDomain);

    // assert
    mockOutEndpoint.assertIsSatisfied();
  }

  @Test
  void
      GIVEN_outEndpointDomainAndTransformation_WHEN_sendingOutEndpointDomainAsPayload_THEN_expectRawStringResultWithSuccessfulValidationAndTransformation()
          throws Exception {
    // arrange
    InConnector inConnector = SimpleInConnector.withEndpoint(InEndpoint.instance(inEndpointUri, DIRECT_IN_ID));
    OutConnectorStub outConnector =
        new OutConnectorStub(
            OutEndpoint.instance(
                "log:foo",
                "outBasicConnector",
                OutEndpointDomain.class,
                OutEndpointDomain::getContentWithMessage));
    OutEndpointDomain outEndpointDomain = new OutEndpointDomain("hello world");
    subject.input(inConnector).sequencedOutput(outConnector);
    subject.toCentralRouter().setUpRoutes();

    mockOutEndpoint.expectedBodiesReceived("hello world success");
    mockOutEndpoint.expectedMessageCount(1);

    // act
    template.sendBody("direct:OutConnectorStub", outEndpointDomain);

    // assert
    mockOutEndpoint.assertIsSatisfied();
  }

  @Test
  void GIVEN_outEndpointDomain_WHEN_sendingRawStringAsPayload_THEN_unsuccessfulValidation() {
    // arrange
    InConnector inConnector = SimpleInConnector.withEndpoint(InEndpoint.instance(inEndpointUri, DIRECT_IN_ID));
    OutConnectorStub outConnector =
        new OutConnectorStub(
            OutEndpoint.instance("log:foo", "outBasicConnector", OutEndpointDomain.class));
    subject.input(inConnector).sequencedOutput(outConnector);
    subject.toCentralRouter().setUpRoutes();

    // act & assert
    assertThrows(
        CamelExecutionException.class,
        () -> template.sendBody("direct:OutConnectorStub", "string type value"));
  }
}
