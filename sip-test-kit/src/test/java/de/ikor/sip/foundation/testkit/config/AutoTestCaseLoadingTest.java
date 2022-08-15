package de.ikor.sip.foundation.testkit.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootTest(
    properties = {"sip.testkit.test-cases-path:test-tcd.yml", "sip.testkit.batchTest:true"},
    classes = ConfigurableEnvironment.class)
class AutoTestCaseLoadingTest {

  private static final String ROUTE_CONTROLLER_SUPERVISE =
      "camel.springboot.routeControllerSuperviseEnabled";

  @Autowired private ConfigurableEnvironment environment;

  @Test
  void GIVEN_necessaryInputProperties_WHEN_testKitTests_THEN_getExpectedProperties() {
    // arrange
    AutoTestCaseLoading subject = new AutoTestCaseLoading();

    // act
    subject.testKitTests(environment);

    // assert
    assertThat(environment.getProperty("test-case-definitions[0].title")).isEqualTo("test");
    assertThat(environment.getProperty(ROUTE_CONTROLLER_SUPERVISE)).isEqualTo("true");
  }
}
