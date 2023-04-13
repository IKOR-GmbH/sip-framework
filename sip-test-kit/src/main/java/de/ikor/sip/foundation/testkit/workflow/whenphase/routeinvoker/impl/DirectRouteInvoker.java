package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static de.ikor.sip.foundation.testkit.util.TestKitHelper.unmarshallExchangeBodyFromJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.testkit.util.TestKitHelper;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.camel.*;
import org.apache.camel.component.direct.DirectEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Invoker class for triggering routes with Direct consumer. Used for tests which are being started
 * with connector id
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "sip.core.declarativestructure.enabled", havingValue = "true")
public class DirectRouteInvoker implements RouteInvoker {

  public static final String CONNECTOR_ID_EXCHANGE_PROPERTY = "connectorId";
  public static final Class<?> STRING_CLASS = String.class;

  private final ProducerTemplate producerTemplate;

  private final CamelContext camelContext;

  private final DeclarationsRegistryApi declarationsRegistry;

  private final ObjectMapper mapper;

  @Override
  public Optional<Exchange> invoke(Exchange inputExchange) {
    Endpoint endpoint = TestKitHelper.resolveEndpoint(inputExchange, camelContext);
    unmarshallExchangeBody(inputExchange);
    return Optional.of(producerTemplate.send(endpoint, inputExchange));
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return endpoint instanceof DirectEndpoint;
  }

  private void unmarshallExchangeBody(Exchange inputExchange) {
    Optional<ConnectorDefinition> connector =
        declarationsRegistry.getConnectorById(
            (String) inputExchange.getProperty(CONNECTOR_ID_EXCHANGE_PROPERTY));
    Class<?> requestModelClass = getRequestModelClass(connector);

    if (!requestModelClass.equals(STRING_CLASS)) {
      unmarshallExchangeBodyFromJson(inputExchange, mapper, requestModelClass);
    }
  }

  private Class<?> getRequestModelClass(Optional<ConnectorDefinition> connector) {
    if (connector.isPresent()) {
      return connector.get().getRequestModelClass();
    }
    return STRING_CLASS;
  }
}
