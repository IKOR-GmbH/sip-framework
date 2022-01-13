package de.ikor.sip.foundation.core.premiumsupport.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.UUID;
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

class RegistrationWebClientTest {

  private static final String DEMO_DOMAIN = "http://mydomain.com";

  @Mock private RestTemplate restTemplate;
  @Mock private TelemetryDataCollector telemetryDataCollector;
  @Mock private SIPRegistrationProperties properties;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private RestTemplateBuilder restTemplateBuilder;

  private ListAppender<ILoggingEvent> listAppender;
  private RegistrationWebClient subject;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    when(restTemplateBuilder
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setConnectTimeout(properties.getConnectTimeout())
            .setReadTimeout(properties.getReadTimeout())
            .build())
        .thenReturn(restTemplate);
    subject = new RegistrationWebClient(telemetryDataCollector, restTemplateBuilder, properties);

    Logger logger = (Logger) LoggerFactory.getLogger(RegistrationWebClient.class);
    logger.setLevel(Level.DEBUG);
    listAppender = new ListAppender<>();
    listAppender.start();

    logger.addAppender(listAppender);
  }

  @Test
  void When_registerAdapterIsCalled_Expect_PostWithTelemetryDataIsSent() {
    // arrange
    when(properties.getCheckInUrl()).thenReturn(DEMO_DOMAIN);

    when(restTemplate.exchange(
            properties.getCheckInUrl(),
            HttpMethod.POST,
            new HttpEntity<>(telemetryDataCollector.collectData()),
            String.class))
        .thenReturn(null);

    TelemetryData telemetryDataMock = mock(TelemetryData.class);
    when(telemetryDataCollector.collectData()).thenReturn(telemetryDataMock);

    // act
    subject.sendTelemetryData();

    // assert
    verify(restTemplate, times(1))
        .exchange(
            properties.getCheckInUrl(),
            HttpMethod.POST,
            new HttpEntity<>(telemetryDataCollector.collectData()),
            String.class);
  }

  @Test
  void When_unregisterAdapterIsCalled_Expect_DeleteWithoutTelemetryDataIsSent() {
    // arrange
    UUID testID = UUID.randomUUID();
    when(properties.getCheckOutUrl()).thenReturn(DEMO_DOMAIN);
    when(properties.getInstanceId()).thenReturn(testID);
    String checkoutUrl = properties.getCheckOutUrl() + "/" + testID;
    when(restTemplate.exchange(checkoutUrl, HttpMethod.DELETE, null, String.class))
        .thenReturn(null);

    TelemetryData telemetryDataMock = mock(TelemetryData.class);
    when(telemetryDataCollector.collectData()).thenReturn(telemetryDataMock);

    // act
    subject.unregisterAdapter();

    // assert
    verify(restTemplate, times(1)).exchange(checkoutUrl, HttpMethod.DELETE, null, String.class);
  }

  @Test
  void When_communicationExceptionHappens_Expect_warnMessageWithProperArgumentsIsLogged() {
    // arrange
    when(properties.getCheckInUrl()).thenReturn(DEMO_DOMAIN);

    when(restTemplate.exchange(
            properties.getCheckInUrl(),
            HttpMethod.POST,
            new HttpEntity<>(telemetryDataCollector.collectData()),
            String.class))
        .thenThrow(new RestClientException("Simulated error"));
    // act
    subject.sendTelemetryData();
    // assert
    ILoggingEvent logEvent = listAppender.list.get(1);
    assertThat(logEvent.getArgumentArray()).contains("registration", DEMO_DOMAIN);
    assertThat(logEvent.getLevel()).isEqualTo(Level.WARN);
  }
}
