package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TraceOperationConfiguration.class)
@TestPropertySource("classpath:config-test.properties")
@EnableConfigurationProperties(value = SIPTraceConfig.class)
class TraceOperationConfigurationTest {
  @Autowired Set<SIPTraceOperation> sipTraceOperations;

  @Test
  void When_Configuration_With_Asterisk_Expect_AllEnumsRegistered() {
    assertThat(sipTraceOperations)
        .contains(SIPTraceOperation.LOG)
        .contains(SIPTraceOperation.MEMORY);
  }
}
