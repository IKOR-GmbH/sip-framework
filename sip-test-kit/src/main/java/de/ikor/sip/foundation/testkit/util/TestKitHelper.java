package de.ikor.sip.foundation.testkit.util;

import static de.ikor.sip.foundation.core.proxies.ProcessorProxy.TEST_MODE_HEADER;
import static de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker.TEST_NAME_HEADER;
import static org.apache.camel.builder.ExchangeBuilder.anExchange;

import de.ikor.sip.foundation.core.util.SIPExchangeHelper;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;

/** Utility class that changes the {@link Exchange} */
@Slf4j
public class TestKitHelper extends SIPExchangeHelper {

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
      throw new SIPFrameworkException(
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
   * Create exchange from test definition
   *
   * @param properties with route id and payload for exchange body
   * @param camelContext camel context
   * @return {@link Exchange}
   */
  public static Exchange parseExchangeProperties(
      EndpointProperties properties, CamelContext camelContext) {
    if (properties == null) {
      return anExchange(camelContext).build();
    }
    ExchangeBuilder exchangeBuilder =
        anExchange(camelContext).withBody(properties.getMessage().getBody());
    properties.getMessage().getHeaders().forEach(exchangeBuilder::withHeader);
    exchangeBuilder.withProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, properties.getEndpoint());
    return exchangeBuilder.build();
  }

  /**
   * Checks if header is Test Kit specific header
   *
   * @param key of header for checking
   * @return boolean
   */
  public static boolean isTestKitHeader(String key) {
    return key.equals(TEST_NAME_HEADER) || key.equals(TEST_MODE_HEADER);
  }
}
