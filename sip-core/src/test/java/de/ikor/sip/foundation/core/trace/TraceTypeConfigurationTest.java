package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes= TraceTypeConfiguration.class)
@TestPropertySource("classpath:config-test.properties")
@EnableConfigurationProperties(value = SIPTraceConfig.class)
class TraceTypeConfigurationTest {
  @Autowired
  Set<SIPTraceTypeEnum> sipTraceTypeEnums;

  @Test
  void When_Configuration_With_Asterisk_Expect_AllEnumsRegistered() {
    assertThat(sipTraceTypeEnums).contains(SIPTraceTypeEnum.LOG).contains(SIPTraceTypeEnum.MEMORY);
  }
}
