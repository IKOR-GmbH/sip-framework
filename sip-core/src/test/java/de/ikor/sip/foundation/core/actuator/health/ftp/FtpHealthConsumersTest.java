package de.ikor.sip.foundation.core.actuator.health.ftp;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.ikor.sip.foundation.core.actuator.common.IntegrationManagementException;
import java.util.List;
import org.apache.camel.component.file.remote.RemoteFileConfiguration;
import org.apache.camel.component.file.remote.RemoteFileEndpoint;
import org.apache.camel.component.file.remote.RemoteFileOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class FtpHealthConsumersTest {

  ListAppender<ILoggingEvent> listAppender;
  RemoteFileOperations<Object> fileOps;
  RemoteFileEndpoint<?> endpoint;
  RemoteFileConfiguration configuration;

  @BeforeEach
  void setUp() {
    fileOps = mock(RemoteFileOperations.class);
    endpoint = mock(RemoteFileEndpoint.class);
    configuration = mock(RemoteFileConfiguration.class);
    Logger logger = (Logger) LoggerFactory.getLogger(FtpHealthConsumers.class);
    listAppender = new ListAppender<>();
    listAppender.start();

    logger.addAppender(listAppender);
  }

  @Test
  void executeNoop_sendNoopFalse() {
    // arrange
    when(endpoint.getConfiguration()).thenReturn(null);
    when(fileOps.sendNoop()).thenReturn(false);
    when(fileOps.connect(any(), any())).thenReturn(true);

    // act
    FtpHealthConsumers.noopConsumer(endpoint, fileOps);
    List<ILoggingEvent> logsListSubject = listAppender.list;

    // assert
    assertThat(logsListSubject.get(0).getMessage()).isEqualTo("Connected and authenticated to: {}");
    assertThat(logsListSubject.get(0).getLevel()).isEqualTo(Level.DEBUG);
  }

  @Test
  void executeNoop_Exception() {
    // arrange
    when(endpoint.getConfiguration()).thenReturn(null);
    when(fileOps.sendNoop()).thenThrow(new RuntimeException());
    when(fileOps.connect(any(), any())).thenReturn(true);

    // act
    FtpHealthConsumers.noopConsumer(endpoint, fileOps);
    List<ILoggingEvent> logsListSubject = listAppender.list;

    // assert
    assertThat((logsListSubject.get(0).getMessage())).isEqualTo("Failed to sendNoop to {}: {}");
    assertThat(logsListSubject.get(0).getLevel()).isEqualTo(Level.DEBUG);
  }

  @Test
  void When_executeNoopAndSendNoopTrue_Expect_emptyLogList() {
    // arrange
    when(endpoint.getConfiguration()).thenReturn(null);
    when(fileOps.sendNoop()).thenReturn(true);
    when(fileOps.connect(any(), any())).thenReturn(true);

    // act
    FtpHealthConsumers.noopConsumer(endpoint, fileOps);
    List<ILoggingEvent> logsListSubject = listAppender.list;

    // assert
    assertThat(logsListSubject).isEmpty();
  }

  @Test
  void When_executeNoopAndOperationConnectFalse_Expect_emptyLogList() {
    // arrange
    when(endpoint.getConfiguration()).thenReturn(null);
    when(fileOps.sendNoop()).thenReturn(false);
    when(fileOps.connect(any(), any())).thenReturn(false);

    // act
    FtpHealthConsumers.noopConsumer(endpoint, fileOps);
    List<ILoggingEvent> logsListSubject = listAppender.list;

    // assert
    assertThat(logsListSubject).isEmpty();
  }

  @Test
  void When_executeListDirAndStatusUP_Expect_emptyLogList() {
    // arrange
    String directoryName = "dirName";
    when(configuration.getDirectoryName()).thenReturn(directoryName);
    when(endpoint.getConfiguration()).thenReturn(configuration);
    when(fileOps.existsFile(directoryName)).thenReturn(true);
    when(fileOps.listFiles(directoryName)).thenReturn(new Object[0]);

    // act
    FtpHealthConsumers.listDirectoryConsumer(endpoint, fileOps);
    List<ILoggingEvent> logsListSubject = listAppender.list;

    // assert
    assertThat(logsListSubject).isEmpty();
  }

  @Test
  void When_executeListDirAndUnknownFolder_Expect_folderNotFoundMessage() {
    // arrange
    String directoryName = "dirName";
    when(configuration.getDirectoryName()).thenReturn(directoryName);
    when(endpoint.getConfiguration()).thenReturn(configuration);
    when(fileOps.existsFile(directoryName)).thenReturn(false);

    // act
    Exception exceptionSubject =
        assertThrows(
            IntegrationManagementException.class,
            () -> FtpHealthConsumers.listDirectoryConsumer(endpoint, fileOps));

    // assert
    assertThat(exceptionSubject.getMessage()).isEqualTo("Folder " + directoryName + "not found");
  }
}
