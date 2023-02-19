package de.ikor.sip.foundation.core.declarative.model;

import lombok.experimental.UtilityClass;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.support.TypeConverterSupport;

@UtilityClass
public final class ModelMapperHelper {

  @SuppressWarnings("unchecked")
  public static void registerMapperAsTypeConverters(
      final ModelMapper mapper, final TypeConverterRegistry typeConverterRegistry) {
    typeConverterRegistry.addTypeConverter(
        mapper.getScenarioModelClass(),
        mapper.getConnectorModelClass(),
        new TypeConverterSupport() {
          @Override
          public <T> T convertTo(final Class<T> type, final Exchange exchange, final Object value)
              throws TypeConversionException {
            return (T) mapper.mapConnectorToScenarioModel(value);
          }
        });
    typeConverterRegistry.addTypeConverter(
        mapper.getConnectorModelClass(),
        mapper.getScenarioModelClass(),
        new TypeConverterSupport() {
          @Override
          public <T> T convertTo(final Class<T> type, final Exchange exchange, final Object value)
              throws TypeConversionException {
            return (T) mapper.mapScenarioToConnectorModel(value);
          }
        });
  }
}
