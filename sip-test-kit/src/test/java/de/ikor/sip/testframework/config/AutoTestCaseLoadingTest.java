package de.ikor.sip.testframework.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "sip.testkit.test-cases-path:test-tcd.yml", classes = ConfigurableEnvironment.class)
class AutoTestCaseLoadingTest {
    @Autowired
    private ConfigurableEnvironment environment;

    @Test
    void When_testKitTests_Expect_PropertiesFromFileInEnv() {
        // arrange
        AutoTestCaseLoading autoTestCaseLoading = new AutoTestCaseLoading();

        // act
        autoTestCaseLoading.testKitTests(environment);

        // assert
        assertThat(environment.getProperty("test-case-definitions[0].title")).isEqualTo("test");
    }
}