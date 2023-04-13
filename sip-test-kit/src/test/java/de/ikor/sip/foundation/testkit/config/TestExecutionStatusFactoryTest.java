package de.ikor.sip.foundation.testkit.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.ikor.sip.foundation.testkit.configurationproperties.TestCaseDefinition;
import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
import de.ikor.sip.foundation.testkit.workflow.TestExecutionStatus;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CamelContext.class)
class TestExecutionStatusFactoryTest {

  public static final String ENDPOINT_ID = "endpoint";

  private TestExecutionStatusFactory subject;
  private TestCaseDefinition testCaseDefinition;
  private ExtendedCamelContext camelContext;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
    subject = new TestExecutionStatusFactory(camelContext);
    EndpointProperties whenExecute = new EndpointProperties();
    whenExecute.setEndpointId(ENDPOINT_ID);
    testCaseDefinition = new TestCaseDefinition();
    testCaseDefinition.setTitle("title");
    testCaseDefinition.setWhenExecute(whenExecute);
  }

  @Test
  void When_generateTestReport_With_WhenExecute_Then_ExpectedResponseExists() {
    // arrange
    EndpointProperties thenExpectResponse = new EndpointProperties();
    thenExpectResponse.setEndpointId(ENDPOINT_ID);
    List<EndpointProperties> thenExpect = new ArrayList<>();
    thenExpect.add(thenExpectResponse);
    testCaseDefinition.setThenExpect(thenExpect);

    // act
    TestExecutionStatus testExecutionStatus = subject.generateTestReport(testCaseDefinition);

    // assert
    assertThat(testExecutionStatus.getTestName()).isEqualTo("title");
    assertThat(testExecutionStatus.getAdapterReport().getExpectedResponse()).isNotNull();
  }

  @Test
  void When_generateTestReport_With_missingWhenExecute_Then_emptyExchange() {
    // arrange
    Exchange expected = createEmptyExchange(camelContext);

    // act
    TestExecutionStatus testExecutionStatus = subject.generateTestReport(testCaseDefinition);

    // assert
    assertThat(testExecutionStatus.getTestName()).isEqualTo("title");
    assertThat(testExecutionStatus.getAdapterReport().getExpectedResponse().getMessage().getBody())
        .isNull();
    assertThat(
            testExecutionStatus.getAdapterReport().getExpectedResponse().getMessage().getHeaders())
        .isEmpty();
  }

  private Exchange createEmptyExchange(CamelContext camelContext) {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }
}
