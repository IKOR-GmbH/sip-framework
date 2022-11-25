package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.apps.core.CoreTestApplication;
import de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;
import de.ikor.sip.foundation.core.framework.stubs.*;
import org.apache.camel.*;
import org.apache.camel.builder.AdviceWith;
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
@SpringBootTest(classes = CoreTestApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@DisableJmx(false)
@MockEndpoints("log:message*")
class EndpointDomainTests {

    private static final String DIRECT_IN = "direct:in";
    private static final String DIRECT_IN_ID = "inBasicConnector";

    private final TestingCentralRouterDefinition subject = new TestingCentralRouterDefinition();

    @Autowired(required = false)
    private RouteStarter routeStarter;

    @Autowired private ProducerTemplate template;

    @EndpointInject("mock:log:messageIn")
    private MockEndpoint mockInEndpoint;

    @EndpointInject("mock:log:messageOut")
    private MockEndpoint mockOutEndpoint;

    @BeforeEach
    void setup() {
        mockInEndpoint.reset();
        mockOutEndpoint.reset();
    }

    @Test
    void GIVEN_inEndpointDomain_WHEN_sendingInEndpointDomainAsPayload_THEN_successfulValidation() throws Exception {
        // arrange
        InConnectorStub inConnector = new InConnectorStub(InEndpoint.instance(DIRECT_IN, DIRECT_IN_ID, InEndpointDomain.class));
        InEndpointDomain inDomain = new InEndpointDomain("hello world");
        subject.input(inConnector);
        routeStarter.buildRoutes(subject.toCentralRouter());

        String routeId = String.format("%s-%s", subject.getScenario(), inConnector.getName());
        AdviceWith.adviceWith(StaticRouteBuilderHelper.camelContext(), routeId, a ->
                a.mockEndpointsAndSkip(String.format("sipmc:%s", subject.getScenario())));

        mockInEndpoint.expectedBodiesReceived(inDomain);
        mockInEndpoint.expectedMessageCount(1);

        // act
        template.sendBody(DIRECT_IN, inDomain);

        // assert
        mockInEndpoint.assertIsSatisfied();
    }

    @Test
    void GIVEN_inEndpointDomainAndTransformation_WHEN_sendingRawStringAsPayload_THEN_successfulTransformationAndValidation() throws Exception {
        // arrange
        InConnectorStub inConnector = new InConnectorStub(InEndpoint.instance(DIRECT_IN, DIRECT_IN_ID, InEndpointDomain.class, InEndpointDomain::new));
        InEndpointDomain expected = new InEndpointDomain("hello world");
        subject.input(inConnector);
        routeStarter.buildRoutes(subject.toCentralRouter());

        String routeId = String.format("%s-%s", subject.getScenario(), inConnector.getName());
        AdviceWith.adviceWith(StaticRouteBuilderHelper.camelContext(), routeId, a ->
                a.mockEndpointsAndSkip(String.format("sipmc:%s", subject.getScenario())));

        mockInEndpoint.message(0).body(InEndpointDomain.class).contains(expected);
        mockInEndpoint.expectedMessageCount(1);

        // act
        template.sendBody(DIRECT_IN, "hello world");

        // assert
        mockInEndpoint.assertIsSatisfied();
    }

    @Test
    void GIVEN_inEndpointDomain_WHEN_sendingRawStringAsPayload_THEN_unsuccessfulValidation() throws Exception {
        // arrange
        InConnectorStub inConnector = new InConnectorStub(InEndpoint.instance(DIRECT_IN, DIRECT_IN_ID, InEndpointDomain.class));
        subject.input(inConnector);
        routeStarter.buildRoutes(subject.toCentralRouter());

        String routeId = String.format("%s-%s", subject.getScenario(), inConnector.getName());
        AdviceWith.adviceWith(StaticRouteBuilderHelper.camelContext(), routeId, a ->
                a.mockEndpointsAndSkip(String.format("sipmc:%s", subject.getScenario())));

        // act & assert
        assertThrows(CamelExecutionException.class, () -> template.sendBody(DIRECT_IN, "string type value"));
    }

    @Test
    void GIVEN_outEndpointDomain_WHEN_sendingOutEndpointDomainAsPayload_THEN_successfulValidation() throws Exception {
        // arrange
        InConnectorStub inConnector = new InConnectorStub(InEndpoint.instance(DIRECT_IN, DIRECT_IN_ID));
        OutConnectorStub outConnector = new OutConnectorStub(OutEndpoint.instance("log:foo", "outBasicConnector", OutEndpointDomain.class));
        OutEndpointDomain outEndpointDomain = new OutEndpointDomain("hello world");
        subject.input(inConnector).sequencedOutput(outConnector);
        routeStarter.buildRoutes(subject.toCentralRouter());

        String routeId = String.format("%s-%s", subject.getScenario(), outConnector.getName());
        AdviceWith.adviceWith(StaticRouteBuilderHelper.camelContext(), routeId, a ->
                a.mockEndpointsAndSkip("log:foo"));

        mockOutEndpoint.expectedBodiesReceived(outEndpointDomain);
        mockOutEndpoint.expectedMessageCount(1);

        // act
        template.sendBody("direct:BasicOutConnector", outEndpointDomain);

        // assert
        mockOutEndpoint.assertIsSatisfied();
    }

    @Test
    void GIVEN_outEndpointDomainAndTransformation_WHEN_sendingOutEndpointDomainAsPayload_THEN_expectRawStringResultWithSuccessfulValidationAndTransformation() throws Exception {
        // arrange
        InConnectorStub inConnector = new InConnectorStub(InEndpoint.instance(DIRECT_IN, DIRECT_IN_ID));
        OutConnectorStub outConnector = new OutConnectorStub(OutEndpoint.instance("log:foo", "outBasicConnector", OutEndpointDomain.class, OutEndpointDomain::getContentWithMessage));
        OutEndpointDomain outEndpointDomain = new OutEndpointDomain("hello world");
        subject.input(inConnector).sequencedOutput(outConnector);
        routeStarter.buildRoutes(subject.toCentralRouter());

        String routeId = String.format("%s-%s", subject.getScenario(), outConnector.getName());
        AdviceWith.adviceWith(StaticRouteBuilderHelper.camelContext(), routeId, a ->
                a.mockEndpointsAndSkip("log:foo"));

        mockOutEndpoint.expectedBodiesReceived("hello world success");
        mockOutEndpoint.expectedMessageCount(1);

        // act
        template.sendBody("direct:BasicOutConnector", outEndpointDomain);

        // assert
        mockOutEndpoint.assertIsSatisfied();
    }

    @Test
    void GIVEN_outEndpointDomain_WHEN_sendingRawStringAsPayload_THEN_unsuccessfulValidation() throws Exception {
        // arrange
        InConnectorStub inConnector = new InConnectorStub(InEndpoint.instance(DIRECT_IN, DIRECT_IN_ID));
        OutConnectorStub outConnector = new OutConnectorStub(OutEndpoint.instance("log:foo", "outBasicConnector", OutEndpointDomain.class));
        subject.input(inConnector).sequencedOutput(outConnector);
        routeStarter.buildRoutes(subject.toCentralRouter());

        String routeId = String.format("%s-%s", subject.getScenario(), outConnector.getName());
        AdviceWith.adviceWith(StaticRouteBuilderHelper.camelContext(), routeId, a ->
                a.mockEndpointsAndSkip("log:foo"));

        // act & assert
        assertThrows(CamelExecutionException.class, () -> template.sendBody("direct:BasicOutConnector", "string type value"));
    }

}
