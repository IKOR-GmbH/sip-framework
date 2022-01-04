package de.ikor.sip.foundation.core.premiumsupport.registration;

import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

class SIPRegistrationWebClientTest {

  @Mock private RestTemplate restTemplate;
  @Mock private TelemetryDataCollector telemetryDataCollector;
  @Mock private SIPRegistrationProperties properties;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private RestTemplateBuilder restTemplateBuilder;

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

  //    @Test
  //    void test_If_Proper_Deregister_Request_Causes_No_Problems() {
  //      assertDoesNotThrow(
  //          () -> registrationWebClient.sendDeleteRequest("/deregister/" + UUID.randomUUID()));
  //    }
  //
  //    @Test
  //    void test_If_Exception_Caused_By_A_Bad_Register_Request_Is_Handled_And_Causes_No_Problems()
  // {
  //      when(
  //              this.restTemplate.exchange(
  //                  Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Object>)
  //   Mockito.any()))
  //          .thenThrow(new RestClientException("Some Error"));
  //      assertDoesNotThrow(
  //          () -> registrationWebClient.sendPostRequest("/register", new TelemetryData()));
  //    }
  //
  //    @Test
  //    void
  // test_If_Exception_Caused_By_A_Bad_Deregister_Request_Is_Handled_And_Causes_No_Problems()
  //   {
  //      when(
  //              this.restTemplate.exchange(
  //                  Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Object>)
  //   Mockito.any()))
  //          .thenThrow(new RestClientException("Some Error"));
  //      assertDoesNotThrow(
  //          () -> registrationWebClient.sendDeleteRequest("/deregister/" + UUID.randomUUID()));
  //    }
}
