package de.ikor.sip.foundation.core.actuator.declarative;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class DeclarativeEndpointInfoTransformerTest {

  private IntegrationScenarioDefinition scenario;

  private JsonSchemaGenerator schemaGen;
  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  public void setup() {
    scenario = mock(IntegrationScenarioDefinition.class);
    doReturn(String.class).when(scenario).getRequestModelClass();
    schemaGen = mock(JsonSchemaGenerator.class);

    Logger logger =
        (Logger)
            LoggerFactory.getLogger(
                "de.ikor.sip.foundation.core.actuator.declarative.DeclarativeEndpointInfoTransformer");
    logger.setLevel(Level.DEBUG);
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @Test
  void WHEN_createIntegrationScenarioInfo_THEN_validateDebugLogAfterJsonMappingException()
      throws JsonMappingException {
    // arrange
    when(scenario.getPathToDocumentationResource()).thenReturn("");
    doThrow(JsonMappingException.class).when(schemaGen).generateSchema(String.class);

    List<ILoggingEvent> logsList = listAppender.list;

    // act
    DeclarativeEndpointInfoTransformer.createIntegrationScenarioInfo(scenario, schemaGen);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo("sip.core.runtimetest.json.schema_{}");
  }
}
