package de.ikor.sip.foundation.core.actuator.health.ftp;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.actuator.common.IntegrationManagementException;
import org.apache.camel.Endpoint;
import org.apache.camel.component.file.remote.RemoteFileEndpoint;
import org.apache.camel.component.file.remote.RemoteFileOperations;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

class FtpHealthIndicatorsTests {
  @Test
  void noopHealthIndicator_typeMismatch() {
    // act
    Endpoint endpointSubject = mock(Endpoint.class);

    // assert
    assertThatThrownBy(() -> FtpHealthIndicators.noopHealthIndicator(endpointSubject))
        .isInstanceOf(IntegrationManagementException.class);
  }

  @Test
  void noopHealthIndicator_statusUNKNOWN_createRemoteFileOperations_throwsException()
      throws Exception {
    // arrange
    RemoteFileEndpoint endpointSubject = mock(RemoteFileEndpoint.class);

    // act
    when(endpointSubject.createRemoteFileOperations()).thenThrow(new Exception());

    // assert
    assertThat(FtpHealthIndicators.noopHealthIndicator(endpointSubject).getStatus())
        .isEqualTo(Status.UNKNOWN);
  }

  @Test
  void noopHealthIndicator_statusUP_case2() throws Exception {
    // arrange
    RemoteFileEndpoint endpointSubject = mock(RemoteFileEndpoint.class);
    RemoteFileOperations operations = mock(RemoteFileOperations.class);

    // act
    when(endpointSubject.createRemoteFileOperations()).thenReturn(operations);
    when(operations.isConnected()).thenReturn(false);

    // assert
    assertThat(FtpHealthIndicators.noopHealthIndicator(endpointSubject).getStatus())
        .isEqualTo(Status.UP);
  }

  @Test
  void noopHealthIndicator_statusUP() throws Exception {
    // arrange
    RemoteFileEndpoint endpointSubject = mock(RemoteFileEndpoint.class);
    RemoteFileOperations operations = mock(RemoteFileOperations.class);

    // act
    when(endpointSubject.createRemoteFileOperations()).thenReturn(operations);
    when(operations.isConnected()).thenReturn(true);
    when(operations.connect(any(), any())).thenReturn(true);

    // assert
    assertThat(FtpHealthIndicators.noopHealthIndicator(endpointSubject).getStatus())
        .isEqualTo(Status.UP);
  }
}
