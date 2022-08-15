package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.headers.FileExchangeHeaders.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.util.SIPExchangeHelper;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import org.apache.camel.*;
import org.apache.camel.component.file.FileConsumer;
import org.apache.camel.support.EmptyAsyncCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileRouteInvokerTest {

  private static final String ROUTE_ID = "routeId";
  private static final String BODY_PAYLOAD = "Testing body";

  private FileRouteInvoker subject;
  private Exchange inputExchange;
  private AsyncProcessor asyncProcessor;
  private Exchange actualFileExchange;

  @BeforeEach
  void setup() {
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    subject = new FileRouteInvoker(camelContext);
    inputExchange = SIPExchangeHelper.createEmptyExchange(camelContext);
    inputExchange.setProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, ROUTE_ID);
    inputExchange.getMessage().setBody(BODY_PAYLOAD);

    Route route = mock(Route.class);
    FileConsumer fileConsumer = mock(FileConsumer.class);
    asyncProcessor = mock(AsyncProcessor.class);

    when(camelContext.getRoute(ROUTE_ID)).thenReturn(route);
    when(route.getConsumer()).thenReturn(fileConsumer);
    when(fileConsumer.getAsyncProcessor()).thenReturn(asyncProcessor);

    actualFileExchange = SIPExchangeHelper.createEmptyExchange(camelContext);
    when(fileConsumer.createExchange(true)).thenReturn(actualFileExchange);
  }

  @Test
  void
      GIVEN_onlyTestKitPreparedExchangeByDefault_WHEN_invoke_THEN_verifyProcessorCallAndDefaultHeadersAndBody() {
    // act
    subject.invoke(inputExchange, mock(Endpoint.class));

    // assert
    verify(asyncProcessor, times(1)).process(actualFileExchange, EmptyAsyncCallback.get());
    assertThat(actualFileExchange.getMessage().getHeader(CAMEL_FILE_LENGTH.getValue()))
        .isEqualTo((long) BODY_PAYLOAD.length());
    assertThat(actualFileExchange.getMessage().getBody()).isEqualTo(BODY_PAYLOAD);
  }

  @Test
  void GIVEN_filenameHeader_WHEN_invoke_THEN_getFilenameRelatedHeaders() {
    // arrange
    String filename = "testingFile.txt";
    inputExchange.getMessage().setHeader(CAMEL_FILE_NAME.getValue(), filename);

    // act
    subject.invoke(inputExchange, mock(Endpoint.class));

    // assert
    assertThat(actualFileExchange.getMessage().getHeader(CAMEL_FILE_NAME_CONSUMED.getValue()))
        .isEqualTo(filename);
    assertThat(actualFileExchange.getMessage().getHeader(CAMEL_FILE_NAME_ONLY.getValue()))
        .isEqualTo(filename);
  }

  @Test
  void GIVEN_lastModifiedHeader_WHEN_invoke_THEN_getLastModifiedRelatedHeaders() {
    // arrange
    Long lastModifiedTimestamp = Long.valueOf("1658914689700");
    inputExchange
        .getMessage()
        .setHeader(CAMEL_FILE_LAST_MODIFIED.getValue(), lastModifiedTimestamp);

    // act
    subject.invoke(inputExchange, mock(Endpoint.class));

    // assert
    assertThat(actualFileExchange.getMessage().getHeader(CAMEL_MESSAGE_TIMESTAMP.getValue()))
        .isEqualTo(lastModifiedTimestamp);
  }
}
