package de.ikor.sip.foundation.core.declarative.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.UnmarshalDefinition;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnmarshallerDefinitionTest {

  RouteDefinition routeDefinition;

  @BeforeEach
  void setUp() {
    routeDefinition = new RouteDefinition();
  }

  @Test
  void WHEN_unmarshallerUsedWithDataFormatDefinition_THEN_unmarshallerIsAddedToRoute() {
    // arrange
    JaxbDataFormat dataFormatDefinition = new JaxbDataFormat();

    // act
    UnmarshallerDefinition marshallerDefinition =
        UnmarshallerDefinition.forDataFormat(dataFormatDefinition);
    marshallerDefinition.accept(routeDefinition);

    // assert
    assertThat(routeDefinition.getOutputs())
        .hasSize(1)
        .hasExactlyElementsOfTypes(UnmarshalDefinition.class);
    assertThat(((UnmarshalDefinition) routeDefinition.getOutputs().get(0)).getDataFormatType())
        .isInstanceOf(dataFormatDefinition.getClass());
  }

  @Test
  void WHEN_unmarshallerUsedWithDataFormat_THEN_unmarshallerIsAddedToRoute() {
    // arrange
    JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();

    // act
    UnmarshallerDefinition marshallerDefinition =
        UnmarshallerDefinition.forDataFormat(jacksonDataFormat);
    marshallerDefinition.accept(routeDefinition);

    // assert
    assertThat(routeDefinition.getOutputs())
        .hasSize(1)
        .hasExactlyElementsOfTypes(UnmarshalDefinition.class);
    assertThat(
            ((UnmarshalDefinition) routeDefinition.getOutputs().get(0))
                .getDataFormatType()
                .getDataFormat())
        .isInstanceOf(jacksonDataFormat.getClass());
  }
}
