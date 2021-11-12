package de.ikor.sip.foundation.core.translate.logging;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.translate.SIPTranslateMessageService;
import org.apache.camel.Expression;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Processor;
import org.apache.camel.processor.LogProcessor;
import org.apache.camel.spi.CamelLogger;
import org.apache.camel.spi.MaskingFormatter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogProcessorInterceptStrategyTest {
  public static final String TRANSLATED_MESSAGE = "just a message";
  private static final String TRANSLATION_MESSAGE_KEY = "translation.key";
  private static LogProcessorInterceptStrategy objectUnderTest;
  private static SIPTranslateMessageService translateMessageService;

  @Mock LogProcessor subject;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private static ExtendedCamelContext camelContext;

  @BeforeAll
  public static void setup() {
    translateMessageService = mock(SIPTranslateMessageService.class);
    objectUnderTest = new LogProcessorInterceptStrategy(translateMessageService);
  }

  @Test
  void when_ProcessorIsNotLogProcessor_then_ProcessorIsNotChanged() {
    // act
    Processor processor =
        objectUnderTest.wrapProcessorInInterceptors(camelContext, null, subject, null);

    // assert
    assertThat(processor).isEqualTo(subject);
  }

  @Test
  void when_ProcessorIsLogProcessorWithNoExpression_then_ProcessorExpressionIsNotChanged() {
    // arrange
    subject = mock(LogProcessor.class);

    // act
    Processor processor =
        objectUnderTest.wrapProcessorInInterceptors(camelContext, null, subject, null);

    // assert
    assertThat(subject).isEqualTo(processor);
  }

  @Test
  void when_ProcessorIsLogProcessor_then_ProcessorExpressionIsChanged() {
    // arrange
    when(translateMessageService.getTranslatedMessage("translation.key", new Object[0]))
        .thenReturn(TRANSLATED_MESSAGE);

    // act
    Expression translatedExpression = expression(TRANSLATED_MESSAGE);
    when(camelContext.resolveLanguage("simple").createExpression(TRANSLATED_MESSAGE))
        .thenReturn(translatedExpression);

    subject = this.initLogProcessor();
    when(camelContext.adapt(any())).thenReturn(camelContext);

    Processor processor =
        objectUnderTest.wrapProcessorInInterceptors(camelContext, null, subject, null);

    // assert
    assertThat(((LogProcessor) processor).getExpression().toString())
        .hasToString(TRANSLATED_MESSAGE);
  }

  @Test
  void when_ExpressionHasArguments_then_ProcessorExpressionIsChanged() {
    // arrange
    String[] args = {"${body}", "${header.type}"};
    String translatedMessage = TRANSLATED_MESSAGE.concat(args[0]).concat(args[1]);
    when(translateMessageService.getTranslatedMessage("translation.key", args))
        .thenReturn(translatedMessage);

    // act
    Expression translatedExpression = expression(translatedMessage);
    when(camelContext.resolveLanguage("simple").createExpression(translatedMessage))
        .thenReturn(translatedExpression);

    subject = this.initLogProcessor(TRANSLATION_MESSAGE_KEY.concat(" ${body} ${header.type}"));
    when(camelContext.adapt(any())).thenReturn(camelContext);

    Processor processor =
        objectUnderTest.wrapProcessorInInterceptors(camelContext, null, subject, null);

    // assert
    assertThat(translatedMessage).isEqualTo(((LogProcessor) processor).getExpression().toString());
    ;
  }

  private LogProcessor initLogProcessor() {
    return initLogProcessor(TRANSLATION_MESSAGE_KEY);
  }

  private LogProcessor initLogProcessor(String expressionString) {
    LogProcessor logProcessor = mock(LogProcessor.class);

    Expression expression = expression(expressionString);
    when(logProcessor.getExpression()).thenReturn(expression);

    MaskingFormatter maskingFormatterMock = mock(MaskingFormatter.class);
    when(logProcessor.getLogFormatter()).thenReturn(maskingFormatterMock);

    CamelLogger camelLoggerMock = mock(CamelLogger.class);
    when(logProcessor.getLogger()).thenReturn(camelLoggerMock);

    return logProcessor;
  }

  private Expression expression(String expressionString) {
    Expression expression = mock(Expression.class);
    when(expression.toString()).thenReturn(expressionString);
    return expression;
  }
}
