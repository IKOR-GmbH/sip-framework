package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;

class AdapterInfoContributorTest {

  private static final String BUILD_KEY = "build";
  private static final String ADAPTER_NAME_DETAILS_KEY = "adapter-name";
  private static final String ADAPTER_NAME_BUILD_KEY = "name";
  private static final String ADAPTER_VERSION_DETAILS_KEY = "adapter-version";
  private static final String ADAPTER_VERSION_BUILD_KEY = "version";
  private static final String SIP_FRAMEWORK_VERSION_DETAILS_KEY = "sip-framework-version";
  private static final String SIP_FRAMEWORK_VERSION_BUILD_KEY = "sipFrameworkVersion";

  private static final String ADAPTER_NAME_TEST = "testName";
  private static final String ADAPTER_VERSION_TEST = "1.0.2";
  private static final String SIP_FRAMEWORK_VERSION_TEST = "1.0.1";

  private AdapterInfoContributor subject;
  private Info.Builder builder;

  @BeforeEach
  void setup() {
    subject = new AdapterInfoContributor();
    builder = new Info.Builder();
  }

  @Test
  void Given_buildInfo_When_contribute_Then_returnBasicAdapterInfo() {
    // arrange
    builder.withDetail(BUILD_KEY, createBuildInfo());

    // act
    subject.contribute(builder);

    // assert
    LinkedHashMap<String, Object> buildInfoResult =
        (LinkedHashMap<String, Object>) builder.build().get(BUILD_KEY);
    assertThat(buildInfoResult.size()).isEqualTo(3);
    assertThat(buildInfoResult.get(ADAPTER_NAME_DETAILS_KEY)).isEqualTo(ADAPTER_NAME_TEST);
    assertThat(buildInfoResult.get(ADAPTER_VERSION_DETAILS_KEY)).isEqualTo(ADAPTER_VERSION_TEST);
    assertThat(buildInfoResult.get(SIP_FRAMEWORK_VERSION_DETAILS_KEY))
        .isEqualTo(SIP_FRAMEWORK_VERSION_TEST);
  }

  @Test
  void Given_nullBuildInfo_When_contribute_Then_returnNoBasicAdapterInfo() {
    // arrange
    builder.withDetail(BUILD_KEY, null);

    // act
    subject.contribute(builder);

    // assert
    LinkedHashMap<String, Object> buildInfoResult =
        (LinkedHashMap<String, Object>) builder.build().get(BUILD_KEY);
    assertThat(buildInfoResult.size()).isZero();
    assertThat(buildInfoResult.get(ADAPTER_NAME_DETAILS_KEY)).isNull();
    assertThat(buildInfoResult.get(ADAPTER_VERSION_DETAILS_KEY)).isNull();
    assertThat(buildInfoResult.get(SIP_FRAMEWORK_VERSION_DETAILS_KEY)).isNull();
  }

  private LinkedHashMap<String, Object> createBuildInfo() {
    LinkedHashMap<String, Object> buildInfo = new LinkedHashMap<>();
    buildInfo.put(ADAPTER_NAME_BUILD_KEY, ADAPTER_NAME_TEST);
    buildInfo.put(ADAPTER_VERSION_BUILD_KEY, ADAPTER_VERSION_TEST);
    buildInfo.put(SIP_FRAMEWORK_VERSION_BUILD_KEY, SIP_FRAMEWORK_VERSION_TEST);
    return buildInfo;
  }
}
