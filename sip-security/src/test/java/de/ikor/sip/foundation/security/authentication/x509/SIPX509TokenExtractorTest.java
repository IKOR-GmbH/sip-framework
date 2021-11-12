package de.ikor.sip.foundation.security.authentication.x509;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.security.Principal;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class SIPX509TokenExtractorTest {

  private final SIPX509TokenExtractor subject = new SIPX509TokenExtractor();

  @Test
  void WHEN_extract_WITH_validCert_THEN_certExtracted() throws Exception {
    // arrange
    ((Logger) LoggerFactory.getLogger(SIPX509TokenExtractor.class)).setLevel(Level.INFO);
    String expectedDn = "CN=Full Name, EMAILADDRESS=name@domain.de, O=IKOR Gmbh, C=DE";

    X509Certificate certMock = mock(X509Certificate.class);
    Principal principalMock = mock(Principal.class);
    when(certMock.getSubjectDN()).thenReturn(principalMock);
    when(principalMock.toString()).thenReturn(expectedDn);

    X509Certificate[] certs = {certMock};

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("javax.servlet.request.X509Certificate", certs);

    // act
    SIPX509AuthenticationToken result = subject.extract(request);

    // assert
    assertThat(result.getPrincipal()).asString().isEqualTo(expectedDn);
  }

  @Test
  void WHEN_extract_WITH_validCertTraceLogging_THEN_certExtracted() throws Exception {
    // arrange
    ((Logger) LoggerFactory.getLogger(SIPX509TokenExtractor.class)).setLevel(Level.TRACE);
    String expectedDn = "CN=Full Name, EMAILADDRESS=name@domain.de, O=IKOR Gmbh, C=DE";

    X509Certificate certMock = mock(X509Certificate.class);
    Principal principalMock = mock(Principal.class);
    when(certMock.getSubjectDN()).thenReturn(principalMock);
    when(principalMock.toString()).thenReturn(expectedDn);

    X509Certificate[] certs = {certMock};

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("javax.servlet.request.X509Certificate", certs);

    // act
    SIPX509AuthenticationToken result = subject.extract(request);

    // assert
    assertThat(result.getPrincipal()).asString().isEqualTo(expectedDn);
  }

  @Test
  void WHEN_extract_WITH_nullCertificate_THEN_badCredentialsException() throws Exception {
    // arrange
    X509Certificate[] certs = null;

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("javax.servlet.request.X509Certificate", certs);

    // act + assert
    assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> subject.extract(request));
  }

  @Test
  void WHEN_extract_WITH_emptyCertificates_THEN_badCredentialsException() throws Exception {
    // arrange
    X509Certificate[] certs = {};

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("javax.servlet.request.X509Certificate", certs);

    // act + assert
    assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> subject.extract(request));
  }

  @Test
  void WHEN_getTokenType_THEN_returnX509Type() throws Exception {
    assertThat(subject.getTokenType()).isEqualTo(SIPX509AuthenticationToken.class);
  }
}
