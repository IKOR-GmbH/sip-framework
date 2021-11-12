package de.ikor.sip.foundation.core.actuator.health;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.apache.camel.Endpoint;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PathMatcherTest {

  @Test
  void GIVEN_pathExpression_WHEN_evaluated_THEN_matchesFound() {
    // arrange
    Endpoint httpEndpointMock = Mockito.mock(Endpoint.class);
    Endpoint httpsEndpointMock = Mockito.mock(Endpoint.class);

    // act
    when(httpEndpointMock.getEndpointUri()).thenReturn("http://google.com");
    when(httpsEndpointMock.getEndpointUri()).thenReturn("https://google.com");

    PathMatcher matcher = new PathMatcher("http*//**");

    // assert
    assertThat(matcher.test(httpEndpointMock))
        .withFailMessage("mock should be matching the http path but was not")
        .isTrue();
    assertThat(matcher.test(httpsEndpointMock))
        .withFailMessage("mock should be matching the https path but was not")
        .isTrue();
  }

  @Test
  void GIVEN_ftpCompoundExpression_WHEN_evaluated_THAN_matchesFound() {
    // arrange
    Endpoint ftpEndpointMock = Mockito.mock(Endpoint.class);

    // act
    when(ftpEndpointMock.getEndpointUri()).thenReturn("ftp://ikor.de");

    PathMatcher matcher = new PathMatcher("*ftp*//**");

    // assert
    assertThat(matcher.test(ftpEndpointMock)).isTrue();
  }

  @Test
  void GIVEN_sftpCompoundExpression_WHEN_evaluated_THAN_matchesFound() {
    // arrange
    Endpoint sftpEndpointMock = Mockito.mock(Endpoint.class);

    // act
    when(sftpEndpointMock.getEndpointUri()).thenReturn("sftp://ikor.de");

    PathMatcher matcher = new PathMatcher("*ftp*//**");

    // assert
    assertThat(matcher.test(sftpEndpointMock)).isTrue();
  }

  @Test
  void GIVEN_ftpsCompoundExpression_WHEN_evaluated_THAN_matchesFound() {
    // arrange
    Endpoint ftpsEndpointMock = Mockito.mock(Endpoint.class);

    // act
    when(ftpsEndpointMock.getEndpointUri()).thenReturn("ftps://ikor.de");

    PathMatcher matcher = new PathMatcher("*ftp*//**");

    // assert
    assertThat(matcher.test(ftpsEndpointMock)).isTrue();
  }
}
