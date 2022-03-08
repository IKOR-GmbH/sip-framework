package de.ikor.sip.testkit;

import static org.mockito.Mockito.*;

import de.ikor.sip.testkit.workflow.TestExecutionStatus;
import de.ikor.sip.testkit.workflow.reporting.resultprocessor.impl.DefaultReportProcessor;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DefaultReportProcessorTest {

  private static final String TEMPLATE_NAME = "report-template.ftl";

  @Mock private Configuration templateConfiguration;
  @Mock private Template template;
  private DefaultReportProcessor subject;

  @Test
  void When_processTestReport_Expect_TemplateProcessCalled() throws IOException, TemplateException {
    // arrange
    when(templateConfiguration.getTemplate(TEMPLATE_NAME)).thenReturn(template);
    subject = new DefaultReportProcessor(templateConfiguration);
    TestExecutionStatus report = new TestExecutionStatus("test").setSuccessfulExecution(true);

    // act
    subject.process(report);

    // assert
    verify(template, times(1)).process(any(), any());
  }
}
