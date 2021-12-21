package de.ikor.sip.foundation.core.actuator.health;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.apache.camel.Endpoint;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PathMatcherTest {

  @Test
  void GIVEN_httpPathExpression_WHEN_evaluated_THEN_matchesFound() {
    // arrange
    Endpoint httpEndpointMock = Mockito.mock(Endpoint.class);
    PathMatcher subject = new PathMatcher("http*//**");

    // act
    when(httpEndpointMock.getEndpointUri()).thenReturn("http://google.com");

    // assert
    assertThat(subject.test(httpEndpointMock))
        .withFailMessage("mock should be matching the http path but was not")
        .isTrue();
  }

  @Test
  void GIVEN_httpsPathExpression_WHEN_evaluated_THEN_matchesFound() {
    // arrange
    Endpoint httpsEndpointMock = Mockito.mock(Endpoint.class);
    PathMatcher subject = new PathMatcher("http*//**");
    when(httpsEndpointMock.getEndpointUri()).thenReturn("https://google.com");

    // assert
    assertThat(subject.test(httpsEndpointMock))
        .withFailMessage("mock should be matching the https path but was not")
        .isTrue();
  }

  @Test
  void GIVEN_ftpCompoundExpression_WHEN_evaluated_THAN_matchesFound() {
    // arrange
    Endpoint ftpEndpointMock = Mockito.mock(Endpoint.class);
    PathMatcher subject = new PathMatcher("*ftp*//**");
    when(ftpEndpointMock.getEndpointUri()).thenReturn("ftp://ikor.de");

    // assert
    assertThat(subject.test(ftpEndpointMock)).isTrue();
  }

  @Test
  void GIVEN_sftpCompoundExpression_WHEN_evaluated_THAN_matchesFound() {
    // arrange
    Endpoint sftpEndpointMock = Mockito.mock(Endpoint.class);
    PathMatcher subject = new PathMatcher("*ftp*//**");
    when(sftpEndpointMock.getEndpointUri()).thenReturn("sftp://ikor.de");

    // assert
    assertThat(subject.test(sftpEndpointMock)).isTrue();
  }

  @Test
  void GIVEN_ftpsCompoundExpression_WHEN_evaluated_THAN_matchesFound() {
    // arrange
    Endpoint ftpsEndpointMock = Mockito.mock(Endpoint.class);
    PathMatcher subject = new PathMatcher("*ftp*//**");
    when(ftpsEndpointMock.getEndpointUri()).thenReturn("ftps://ikor.de");

    // assert
    assertThat(subject.test(ftpsEndpointMock)).isTrue();
  }
}
