package de.ikor.sip.testframework.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.ikor.sip.testframework.configurationproperties.TestCaseDefinition;
import de.ikor.sip.testframework.configurationproperties.models.EndpointProperties;
import de.ikor.sip.testframework.workflow.TestExecutionStatus;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.ExtendedCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CamelContext.class)
class TestExecutionStatusFactoryTest {

  public static final String ENDPOINT_ID = "endpoint";

  private ExtendedCamelContext camelContext;
  private EndpointProperties whenExecute;
  private TestExecutionStatusFactory subject;
  private TestCaseDefinition testCaseDefinition;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
    subject = new TestExecutionStatusFactory(camelContext);
    whenExecute = new EndpointProperties();
    whenExecute.setEndpoint(ENDPOINT_ID);
    testCaseDefinition = new TestCaseDefinition();
    testCaseDefinition.setTitle("title");
    testCaseDefinition.setWhenExecute(whenExecute);
  }

  @Test
  void When_generateTestReport_With_WhenExecute_Then_ExpectedResponseExists() {
    // arrange
    EndpointProperties thenExpectResponse = new EndpointProperties();
    thenExpectResponse.setEndpoint(ENDPOINT_ID);
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
  void When_generateTestReport_With_missingWhenExecute_Then_NoExpectedResponse() {
    // act
    TestExecutionStatus testExecutionStatus = subject.generateTestReport(testCaseDefinition);

    // assert
    assertThat(testExecutionStatus.getTestName()).isEqualTo("title");
    assertThat(testExecutionStatus.getAdapterReport().getExpectedResponse()).isNull();
  }
}
