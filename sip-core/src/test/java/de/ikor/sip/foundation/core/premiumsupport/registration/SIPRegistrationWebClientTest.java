package de.ikor.sip.foundation.core.premiumsupport.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.ikor.sip.foundation.core.actuator.health.ftp.FtpHealthConsumers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class SIPRegistrationWebClientTest {

  @Mock private RestTemplate restTemplate;
  @Mock private TelemetryDataCollector telemetryDataCollector;
  @Mock private SIPRegistrationProperties properties;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private RestTemplateBuilder restTemplateBuilder;

  private ListAppender<ILoggingEvent> listAppender;
  private SIPRegistrationWebClient subject;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    when(restTemplateBuilder
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setConnectTimeout(properties.getConnectTimeout())
            .setReadTimeout(properties.getReadTimeout())
            .build())
        .thenReturn(restTemplate);
    subject = new SIPRegistrationWebClient(telemetryDataCollector, restTemplateBuilder, properties);

    Logger logger = (Logger) LoggerFactory.getLogger(SIPRegistrationWebClient.class);
    listAppender = new ListAppender<>();
    listAppender.start();

    logger.addAppender(listAppender);
  }

  @Test
  void When_registerAdapterIsCalled_PostWithTelemetryDataIsSent() {
    // arrange
    when(properties.getCheckInUrl()).thenReturn("http://mydomain.com");

    when(restTemplate.exchange(
            properties.getCheckInUrl(),
            HttpMethod.POST,
            new HttpEntity<>(telemetryDataCollector.collectData()),
            String.class))
        .thenReturn(null);

    TelemetryData telemetryDataMock = mock(TelemetryData.class);
    when(telemetryDataCollector.collectData()).thenReturn(telemetryDataMock);

    // act
    subject.registerAdapter();

    // assert
    final ResponseEntity<String> verify =
        verify(restTemplate, times(1))
            .exchange(
                properties.getCheckInUrl(),
                HttpMethod.POST,
                new HttpEntity<>(telemetryDataCollector.collectData()),
                String.class);
  }

  @Test
  void When_unregisterAdapterIsCalled_DeleteWithoutTelemetryDataIsSent() {
    // arrange
    UUID testID = UUID.randomUUID();
    when(properties.getCheckOutUrl()).thenReturn("http://mydomain.com");
    when(properties.getInstanceId()).thenReturn(testID);
    String checkoutUrl = properties.getCheckOutUrl() + "/" + testID;
    when(restTemplate.exchange(checkoutUrl, HttpMethod.DELETE, null, String.class))
        .thenReturn(null);

    TelemetryData telemetryDataMock = mock(TelemetryData.class);
    when(telemetryDataCollector.collectData()).thenReturn(telemetryDataMock);

    // act
    subject.unregisterAdapter();

    // assert
    final ResponseEntity<String> verify =
        verify(restTemplate, times(1)).exchange(checkoutUrl, HttpMethod.DELETE, null, String.class);
  }

  @Test
  void When_communicationExceptionHappens_Expect_warnMessageWithProperArgumentsIsLogged() {
    // arrange
    when(properties.getCheckInUrl()).thenReturn("http://mydomain.com");

    when(restTemplate.exchange(
            properties.getCheckInUrl(),
            HttpMethod.POST,
            new HttpEntity<>(telemetryDataCollector.collectData()),
            String.class))
        .thenThrow(new RestClientException("Simulated error"));
    // act
    subject.registerAdapter();
    // assert
    ILoggingEvent logEvent = listAppender.list.get(1);
    assertThat(logEvent.getArgumentArray()).contains("registration", "http://mydomain.com");
    assertThat(logEvent.getLevel()).isEqualTo(Level.WARN);
  }
}
