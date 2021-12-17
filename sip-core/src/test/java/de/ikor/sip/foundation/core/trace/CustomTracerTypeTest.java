package de.ikor.sip.foundation.core.trace;

import org.apache.camel.CamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = SIPTraceConfig.class)
@TestPropertySource("classpath:config-test.properties")
class CustomTracerTypeTest {

  @Autowired SIPTraceConfig traceConfiguration;

  CustomTracer customTracer;

  @BeforeEach
  void setUp() {
    customTracer =
        new CustomTracer(
            new TraceHistory(traceConfiguration.getLimit()),
            null,
            mock(CamelContext.class),
            traceConfiguration);
  }

  @Test
  void testLogtypeConfiguration() {
    assertThat(traceConfiguration.getTraceType()).isEqualTo(SIPTraceTypeEnum.LOG);
  }
}
