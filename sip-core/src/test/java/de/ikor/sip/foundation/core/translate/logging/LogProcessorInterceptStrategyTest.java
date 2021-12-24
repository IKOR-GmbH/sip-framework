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
  private static LogProcessorInterceptStrategy subject;
  private static SIPTranslateMessageService translateMessageService;

  @Mock private LogProcessor logProcessor;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private static ExtendedCamelContext camelContext;

  @BeforeAll
  public static void setup() {
    translateMessageService = mock(SIPTranslateMessageService.class);
    subject = new LogProcessorInterceptStrategy(translateMessageService);
  }

  @Test
  void When_ProcessorIsNotLogProcessor_Expect_ProcessorIsNotChanged() {
    // arrange
    Processor notLogProcessor = mock(Processor.class);

    // act
    Processor processor =
        subject.wrapProcessorInInterceptors(camelContext, null, notLogProcessor, null);

    // assert
    assertThat(processor).isEqualTo(notLogProcessor);
  }

  @Test
  void When_ProcessorIsLogProcessorWithNoExpression_Expect_ProcessorExpressionIsNotChanged() {
    // act
    Processor processor =
        subject.wrapProcessorInInterceptors(camelContext, null, logProcessor, null);

    // assert
    assertThat(processor).isEqualTo(logProcessor);
    verify(logProcessor, times(1)).getExpression();
  }

  @Test
  void When_ProcessorIsLogProcessor_Expect_ProcessorExpressionIsChanged() {
    // arrange
    when(translateMessageService.getTranslatedMessage("translation.key", new Object[0]))
        .thenReturn(TRANSLATED_MESSAGE);
    Expression translatedExpression = expression(TRANSLATED_MESSAGE);
    when(camelContext.resolveLanguage("simple").createExpression(TRANSLATED_MESSAGE))
        .thenReturn(translatedExpression);

    this.initLogProcessor();
    when(camelContext.adapt(any())).thenReturn(camelContext);

    // act
    Processor processor =
        subject.wrapProcessorInInterceptors(camelContext, null, logProcessor, null);

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

    Expression translatedExpression = expression(translatedMessage);
    when(camelContext.resolveLanguage("simple").createExpression(translatedMessage))
        .thenReturn(translatedExpression);

    this.initLogProcessor(TRANSLATION_MESSAGE_KEY.concat(" ${body} ${header.type}"));
    when(camelContext.adapt(any())).thenReturn(camelContext);

    // act
    Processor processor =
        subject.wrapProcessorInInterceptors(camelContext, null, logProcessor, null);

    // assert
    assertThat(((LogProcessor) processor).getExpression().toString()).isEqualTo(translatedMessage);
  }

  private void initLogProcessor() {
    initLogProcessor(TRANSLATION_MESSAGE_KEY);
  }

  private void initLogProcessor(String expressionString) {
    Expression expression = expression(expressionString);
    when(logProcessor.getExpression()).thenReturn(expression);

    MaskingFormatter maskingFormatterMock = mock(MaskingFormatter.class);
    when(logProcessor.getLogFormatter()).thenReturn(maskingFormatterMock);

    CamelLogger camelLoggerMock = mock(CamelLogger.class);
    when(logProcessor.getLogger()).thenReturn(camelLoggerMock);
  }

  private Expression expression(String expressionString) {
    Expression expression = mock(Expression.class);
    when(expression.toString()).thenReturn(expressionString);
    return expression;
  }
}
