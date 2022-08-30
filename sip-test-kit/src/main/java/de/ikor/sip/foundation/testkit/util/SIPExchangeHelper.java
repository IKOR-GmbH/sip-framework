package de.ikor.sip.foundation.testkit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
import de.ikor.sip.foundation.testkit.configurationproperties.models.MessageProperties;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.support.DefaultExchangeHolder;
import org.apache.camel.support.MessageHelper;

/** Utility class that changes the {@link Exchange} */
@Slf4j
public class SIPExchangeHelper extends DefaultExchangeHolder {

  private static final String SERIALIZABLE_DEFAULT_VALUE = "This is non serializable value";

  /**
   * Filters out all non-serializable headers so they can be used in serializable environment
   *
   * @param exchange whose headers should be checked
   * @return Map of headers that are serializable
   */
  protected static Map<String, Object> filterNonSerializableHeaders(Exchange exchange) {
    Map<String, Object> filteredHeaders = new HashMap<>();
    exchange
        .getMessage()
        .getHeaders()
        .forEach(
            (k, v) -> {
              Object value = getValidHeaderValue(k, v, true);
              if (value != null) {
                filteredHeaders.put(k, reassignNonSerializableValue(k, value));
              }
            });
    return filteredHeaders;
  }

  protected static Object reassignNonSerializableValue(String headerName, Object value) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.writeValue(new ByteArrayOutputStream(), value);
    } catch (IOException e) {
      log.warn("sip.testkit.util.nonserializablevalue_{}", headerName);
      return SERIALIZABLE_DEFAULT_VALUE;
    }
    return value;
  }

  /**
   * Creates a {@link MessageProperties} from the {@link Exchange}
   *
   * @param exchange that should be mapped
   * @return serializable message properties
   */
  public static MessageProperties mapToMessageProperties(Exchange exchange) {
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setBody(MessageHelper.extractBodyAsString(exchange.getMessage()));
    messageProperties.setHeaders(filterNonSerializableHeaders(exchange));
    return messageProperties;
  }

  /**
   * Get route id from the {@link Exchange}
   *
   * @param exchange that should be mapped
   * @return route id
   */
  public static String getRouteId(Exchange exchange) {
    return exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, String.class);
  }

  /**
   * Creates an empty {@link Exchange}
   *
   * @param camelContext is necessary for creation
   * @return exchange
   */
  public static Exchange createEmptyExchange(CamelContext camelContext) {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }

  /**
   * Get camel endpoint based on route id
   *
   * @param exchange for fetching the route id
   * @param camelContext in which routes are defined
   * @return {@link Endpoint}
   */
  public static Endpoint resolveEndpoint(Exchange exchange, CamelContext camelContext) {
    Route route = camelContext.getRoute(getRouteId(exchange));
    if (route == null) {
      throw new IllegalArgumentException(
          "Route with id " + getRouteId(exchange) + " was not found");
    }
    return route.getEndpoint();
  }

  /**
   * Create exchange from test definition
   *
   * @param properties with route id and payload for exchange body
   * @param camelContext camel context
   * @return {@link Exchange}
   */
  public static Exchange parseExchangeProperties(
      EndpointProperties properties, CamelContext camelContext) {
    if (properties == null) {
      return createEmptyExchange(camelContext);
    }
    ExchangeBuilder exchangeBuilder =
        ExchangeBuilder.anExchange(camelContext).withBody(properties.getMessage().getBody());
    properties.getMessage().getHeaders().forEach(exchangeBuilder::withHeader);
    exchangeBuilder.withProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, properties.getEndpoint());
    return exchangeBuilder.build();
  }
}
