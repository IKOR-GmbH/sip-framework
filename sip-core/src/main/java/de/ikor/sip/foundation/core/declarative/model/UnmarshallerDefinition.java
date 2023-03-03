package de.ikor.sip.foundation.core.declarative.model;

import de.ikor.sip.foundation.core.declarative.RouteDefinitionConsumer;
import java.util.function.Consumer;
import org.apache.camel.builder.DataFormatClause;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.DataFormat;

/** Class providing various way to define an unmarshaller */
public interface UnmarshallerDefinition extends RouteDefinitionConsumer {

  /**
   * Creates an unmarshaller definition from a {@link DataFormat} instance
   *
   * @param dataFormat The data format
   * @return The unmarshaller definition
   */
  static UnmarshallerDefinition forDataFormat(final DataFormat dataFormat) {
    return routeBuilder -> routeBuilder.unmarshal(dataFormat);
  }

  /**
   * Creates an unmarshaller definition from a {@link DataFormatDefinition} instance
   *
   * @param dataFormatDefinition The data format definition
   * @return The unmarshaller definition
   */
  static UnmarshallerDefinition forDataFormat(final DataFormatDefinition dataFormatDefinition) {
    return routeBuilder -> routeBuilder.unmarshal(dataFormatDefinition);
  }

  /**
   * Creates an unmarshaller using a consumer for the fluent {@link DataFormatClause} API
   *
   * @param consumer Consumer for fluent API
   * @return The unmarshaller definition
   */
  static UnmarshallerDefinition forClause(
      final Consumer<DataFormatClause<ProcessorDefinition<RouteDefinition>>> consumer) {
    return routeBuilder -> consumer.accept(routeBuilder.unmarshal());
  }
}
