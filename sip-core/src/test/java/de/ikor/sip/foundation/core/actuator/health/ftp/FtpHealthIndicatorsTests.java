package de.ikor.sip.foundation.core.actuator.health.ftp;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.actuator.common.IntegrationManagementException;
import org.apache.camel.Endpoint;
import org.apache.camel.component.file.remote.RemoteFileEndpoint;
import org.apache.camel.component.file.remote.RemoteFileOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

class FtpHealthIndicatorsTests {

  private RemoteFileEndpoint endpoint;

  @BeforeEach
  void setUp() {
    endpoint = mock(RemoteFileEndpoint.class);
  }

  @Test
  void When_noopHealthIndicatorAndNotRemoteFileEndpoint_Expect_IntegrationManagementException() {
    // assert
    assertThatThrownBy(() -> FtpHealthIndicators.noopHealthIndicator(mock(Endpoint.class)))
        .isInstanceOf(IntegrationManagementException.class);
  }

  @Test
  void When_noopHealthIndicatorAndCreateRemoteFileOperationsThrowsException_Expect_statusUnknown()
      throws Exception {
    // act
    when(endpoint.createRemoteFileOperations()).thenThrow(new Exception());

    // assert
    assertThat(FtpHealthIndicators.noopHealthIndicator(endpoint).getStatus())
        .isEqualTo(Status.UNKNOWN);
  }

  @Test
  void When_noopHealthIndicatorAndOperationNotConnected_Expect_statusUp() throws Exception {
    // arrange
    RemoteFileOperations operations = mock(RemoteFileOperations.class);

    // act
    when(endpoint.createRemoteFileOperations()).thenReturn(operations);
    when(operations.isConnected()).thenReturn(false);

    // assert
    assertThat(FtpHealthIndicators.noopHealthIndicator(endpoint).getStatus()).isEqualTo(Status.UP);
  }

  @Test
  void When_noopHealthIndicatorAndOperationConnected_Expect_statusUp() throws Exception {
    // arrange
    RemoteFileOperations operations = mock(RemoteFileOperations.class);

    // act
    when(endpoint.createRemoteFileOperations()).thenReturn(operations);
    when(operations.isConnected()).thenReturn(true);
    when(operations.connect(any(), any())).thenReturn(true);

    // assert
    assertThat(FtpHealthIndicators.noopHealthIndicator(endpoint).getStatus()).isEqualTo(Status.UP);
  }
}
