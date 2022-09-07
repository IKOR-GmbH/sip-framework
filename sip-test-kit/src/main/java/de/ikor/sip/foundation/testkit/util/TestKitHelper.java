package de.ikor.sip.foundation.testkit.util;

import de.ikor.sip.foundation.core.util.SIPExchangeHelper;
import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
import de.ikor.sip.foundation.testkit.configurationproperties.models.MessageProperties;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.support.MessageHelper;

/** Utility class that changes the {@link Exchange} */
@Slf4j
public class TestKitHelper extends SIPExchangeHelper {

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
    return (String) exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY);
  }

  /**
   * Get camel endpoint based on exchange route id
   *
   * @param exchange for fetching the route id
   * @param camelContext in which endpoints are defined
   * @return {@link Endpoint}
   */
  public static Endpoint resolveEndpoint(Exchange exchange, CamelContext camelContext) {
    Route route = resolveRoute(exchange, camelContext);
    if (route == null) {
      throw new IllegalArgumentException(
          "Route with id " + getRouteId(exchange) + " was not found");
    }
    return route.getEndpoint();
  }

  /**
   * Get camel route based on exchange route id
   *
   * @param exchange for fetching the route id
   * @param camelContext in which routes are defined
   * @return {@link Route}
   */
  public static Route resolveRoute(Exchange exchange, CamelContext camelContext) {
    return camelContext.getRoute(getRouteId(exchange));
  }

  /**
   * Get camel consumer based on exchange route id
   *
   * @param exchange for fetching the route id
   * @param camelContext in which consumers are defined
   * @return {@link Route}
   */
  public static Consumer resolveConsumer(Exchange exchange, CamelContext camelContext) {
    return resolveRoute(exchange, camelContext).getConsumer();
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
