package de.ikor.sip.foundation.core.premiumsupport.registration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

class RegistrationWebClientTest {

  @Mock private RestTemplate restTemplate;

  private SIPRegistrationWebClient SIPRegistrationWebClient;

//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.initMocks(this);
//    registrationWebClient =
//        new RegistrationWebClient(
//            restTemplate, "http://127.0.0.1:8082/api/v1/client", 5000L, 5000L);
//  }
//
//  @Test
//  void test_If_Proper_Register_Request_Causes_No_Problems() {
//    assertDoesNotThrow(
//        () -> registrationWebClient.sendPostRequest("/register", new TelemetryData()));
//  }
//
//  @Test
//  void test_If_Proper_Deregister_Request_Causes_No_Problems() {
//    assertDoesNotThrow(
//        () -> registrationWebClient.sendDeleteRequest("/deregister/" + UUID.randomUUID()));
//  }
//
//  @Test
//  void test_If_Exception_Caused_By_A_Bad_Register_Request_Is_Handled_And_Causes_No_Problems() {
//    Mockito.when(
//            this.restTemplate.exchange(
//                Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Object>) Mockito.any()))
//        .thenThrow(new RestClientException("Some Error"));
//    assertDoesNotThrow(
//        () -> registrationWebClient.sendPostRequest("/register", new TelemetryData()));
//  }
//
//  @Test
//  void test_If_Exception_Caused_By_A_Bad_Deregister_Request_Is_Handled_And_Causes_No_Problems() {
//    Mockito.when(
//            this.restTemplate.exchange(
//                Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Object>) Mockito.any()))
//        .thenThrow(new RestClientException("Some Error"));
//    assertDoesNotThrow(
//        () -> registrationWebClient.sendDeleteRequest("/deregister/" + UUID.randomUUID()));
//  }
}
