package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.RouteDefinitionConsumer;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.apache.camel.builder.DataFormatClause;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.DataFormat;

/** Class providing various way to define a marshaller */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MarshallerDefinition implements RouteDefinitionConsumer {
  @Delegate private final RouteDefinitionConsumer consumer;

  /**
   * Creates a marshaller definition from a {@link DataFormat} instance
   *
   * @param dataFormat The data format
   * @return The marshaller definition
   */
  public static MarshallerDefinition forDataFormat(final DataFormat dataFormat) {
    return new MarshallerDefinition(routeBuilder -> routeBuilder.marshal(dataFormat));
  }

  /**
   * Creates a marshaller definition from a {@link DataFormatDefinition} instance
   *
   * @param dataFormatDefinition The data format definition
   * @return The marshaller definition
   */
  public static MarshallerDefinition forDataFormat(
      final DataFormatDefinition dataFormatDefinition) {
    return new MarshallerDefinition(routeBuilder -> routeBuilder.marshal(dataFormatDefinition));
  }

  /**
   * Creates a marshaller using a consumer for the fluent {@link DataFormatClause} API
   *
   * @param consumer Consumer for fluent API
   * @return The marshaller definition
   */
  public static MarshallerDefinition forDataFormatClause(
      final Consumer<DataFormatClause<ProcessorDefinition<RouteDefinition>>> consumer) {
    return new MarshallerDefinition(routeBuilder -> consumer.accept(routeBuilder.marshal()));
  }
}
