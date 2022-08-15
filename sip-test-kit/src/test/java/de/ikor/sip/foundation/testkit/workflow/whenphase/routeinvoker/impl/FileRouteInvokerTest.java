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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FileRouteInvokerTest {

  private static final String ROUTE_ID = "routeId";
  private static final String BODY_PAYLOAD = "Testing body";
  private static final String NO_HEADER = "no header";
  private static final String TESTING_FILE_NAME = "testingFile.txt";

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

  @ParameterizedTest(name = "Using input header CamelFileLength: {0}")
  @ValueSource(strings = {NO_HEADER, "15"})
  void
      GIVEN_onlyTestKitPreparedExchangeByDefault_WHEN_invoke_THEN_verifyProcessorCallAndDefaultHeadersAndBody(
          String lengthValue) {
    // arrange
    if (isHeaderProvidedInConfiguration(lengthValue)) {
      inputExchange.getMessage().setHeader(CAMEL_FILE_LENGTH.getValue(), Long.valueOf(lengthValue));
    }

    // act
    subject.invoke(inputExchange, mock(Endpoint.class));

    // assert
    verify(asyncProcessor, times(1)).process(actualFileExchange, EmptyAsyncCallback.get());
    assertThat(actualFileExchange.getMessage().getBody()).isEqualTo(BODY_PAYLOAD);
    assertThat(actualFileExchange.getMessage().getHeader(CAMEL_FILE_LENGTH.getValue()))
        .isEqualTo(
            isHeaderProvidedInConfiguration(lengthValue)
                ? Long.parseLong(lengthValue)
                : (long) BODY_PAYLOAD.length());
  }

  @ParameterizedTest(name = "Using input header CamelFileNameConsumed: {0}")
  @ValueSource(strings = {NO_HEADER, "inputFile.txt"})
  void GIVEN_filenameHeader_WHEN_invoke_THEN_getCamelFileNameConsumedHeader(
      String camelFileNameConsumed) {
    // arrange
    inputExchange.getMessage().setHeader(CAMEL_FILE_NAME.getValue(), TESTING_FILE_NAME);
    if (isHeaderProvidedInConfiguration(camelFileNameConsumed)) {
      inputExchange
          .getMessage()
          .setHeader(CAMEL_FILE_NAME_CONSUMED.getValue(), camelFileNameConsumed);
    }

    // act
    subject.invoke(inputExchange, mock(Endpoint.class));

    // assert
    assertThat(actualFileExchange.getMessage().getHeader(CAMEL_FILE_NAME_CONSUMED.getValue()))
        .isEqualTo(
            isHeaderProvidedInConfiguration(camelFileNameConsumed)
                ? camelFileNameConsumed
                : TESTING_FILE_NAME);
  }

  @ParameterizedTest(name = "Using input header CamelFileNameOnly: {0}")
  @ValueSource(strings = {NO_HEADER, "inputFile.txt"})
  void GIVEN_filenameHeader_WHEN_invoke_THEN_getCamelFileNameOnlyHeader(String camelFileNameOnly) {
    // arrange
    inputExchange.getMessage().setHeader(CAMEL_FILE_NAME.getValue(), TESTING_FILE_NAME);
    if (isHeaderProvidedInConfiguration(camelFileNameOnly)) {
      inputExchange.getMessage().setHeader(CAMEL_FILE_NAME_ONLY.getValue(), camelFileNameOnly);
    }

    // act
    subject.invoke(inputExchange, mock(Endpoint.class));

    // assert
    assertThat(actualFileExchange.getMessage().getHeader(CAMEL_FILE_NAME_ONLY.getValue()))
        .isEqualTo(
            isHeaderProvidedInConfiguration(camelFileNameOnly)
                ? camelFileNameOnly
                : TESTING_FILE_NAME);
  }

  @ParameterizedTest(name = "Using input header CamelMessageTimestamp: {0}")
  @ValueSource(strings = {NO_HEADER, "16589146810000"})
  void GIVEN_lastModifiedHeader_WHEN_invoke_THEN_getCamelMessageTimestampHeader(
      String camelMessageTimestamp) {
    // arrange
    Long lastModifiedTimestamp = Long.valueOf("1658914689700");
    inputExchange
        .getMessage()
        .setHeader(CAMEL_FILE_LAST_MODIFIED.getValue(), lastModifiedTimestamp);
    if (isHeaderProvidedInConfiguration(camelMessageTimestamp)) {
      inputExchange
          .getMessage()
          .setHeader(CAMEL_MESSAGE_TIMESTAMP.getValue(), camelMessageTimestamp);
    }

    // act
    subject.invoke(inputExchange, mock(Endpoint.class));

    // assert
    assertThat(actualFileExchange.getMessage().getHeader(CAMEL_MESSAGE_TIMESTAMP.getValue()))
        .isEqualTo(
            isHeaderProvidedInConfiguration(camelMessageTimestamp)
                ? camelMessageTimestamp
                : lastModifiedTimestamp);
  }

  private boolean isHeaderProvidedInConfiguration(String headerValue) {
    return !headerValue.equals(NO_HEADER);
  }
}
