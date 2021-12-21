package de.ikor.sip.foundation.core.actuator.health.ftp;

import static org.assertj.core.api.Assertions.*;
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

  private ListAppender<ILoggingEvent> listAppender;
  private RemoteFileOperations<Object> fileOps;
  private RemoteFileEndpoint<?> endpoint;
  private RemoteFileConfiguration configuration;
  private List<ILoggingEvent> logsList;

  @BeforeEach
  void setUp() {
    fileOps = mock(RemoteFileOperations.class);
    endpoint = mock(RemoteFileEndpoint.class);
    configuration = mock(RemoteFileConfiguration.class);
    Logger logger = (Logger) LoggerFactory.getLogger(FtpHealthConsumers.class);
    listAppender = new ListAppender<>();
    listAppender.start();

    logger.addAppender(listAppender);
    logsList = listAppender.list;
  }

  @Test
  void When_noopConsumerAndSendNoopNotSuccessfulAndConnectSuccessful_Expect_connectedMessage() {
    // arrange
    when(endpoint.getConfiguration()).thenReturn(null);
    when(fileOps.sendNoop()).thenReturn(false);
    when(fileOps.connect(any(), any())).thenReturn(true);

    // act
    FtpHealthConsumers.noopConsumer(endpoint, fileOps);
    ILoggingEvent subject = logsList.get(0);

    // assert
    assertThat(subject.getMessage()).isEqualTo("Connected and authenticated to: {}");
    assertThat(subject.getLevel()).isEqualTo(Level.DEBUG);
  }

  @Test
  void When_noopConsumerAndSendNoopThrowsException_failMessage() {
    // arrange
    when(endpoint.getConfiguration()).thenReturn(null);
    when(fileOps.sendNoop()).thenThrow(new RuntimeException());
    when(fileOps.connect(any(), any())).thenReturn(true);

    // act
    FtpHealthConsumers.noopConsumer(endpoint, fileOps);
    ILoggingEvent subject = logsList.get(0);

    // assert
    assertThat(subject.getMessage()).isEqualTo("Failed to sendNoop to {}: {}");
    assertThat(subject.getLevel()).isEqualTo(Level.DEBUG);
  }

  @Test
  void When_executeNoopAndSendNoopSuccessful_Expect_emptyLogList() {
    // arrange
    when(endpoint.getConfiguration()).thenReturn(null);
    when(fileOps.sendNoop()).thenReturn(true);
    when(fileOps.connect(any(), any())).thenReturn(true);

    // act
    FtpHealthConsumers.noopConsumer(endpoint, fileOps);

    // assert
    assertThat(logsList).isEmpty();
  }

  @Test
  void When_executeNoopAndOperationConnectUnsuccessful_Expect_emptyLogList() {
    // arrange
    when(endpoint.getConfiguration()).thenReturn(null);
    when(fileOps.sendNoop()).thenReturn(false);
    when(fileOps.connect(any(), any())).thenReturn(false);

    // act
    FtpHealthConsumers.noopConsumer(endpoint, fileOps);

    // assert
    assertThat(logsList).isEmpty();
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

    // assert
    assertThat(logsList).isEmpty();
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
        catchThrowableOfType(
            () -> FtpHealthConsumers.listDirectoryConsumer(endpoint, fileOps),
            IntegrationManagementException.class);

    // assert
    assertThat(exceptionSubject.getMessage()).isEqualTo("Folder " + directoryName + "not found");
  }
}
