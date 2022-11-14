package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.templates.FromDirectOutConnectorRouteTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplate;
import de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RouteBinder {
  private final String useCase;
  private final Class<?> centralModelRequest;
  protected final String suffix;
  @Getter protected final List<RouteBuilder> outConnectorsBuilders = new ArrayList<>();

  protected void appendOutConnectorsSeq(OutConnector[] outConnectors) {
    appendConnectors(outConnectors, false);
  }

  public void appendOutConnectorsParallel(OutConnector[] outConnectors) {
    appendConnectors(outConnectors, true);
  }

  private void appendConnectors(OutConnector[] outConnectors, boolean isParallel) {
    FromMiddleComponentRouteTemplate.withUseCase(useCase)
        .withSuffix(suffix)
        .withCentralDomainRequest(centralModelRequest)
        .outConnectors(outConnectors)
        .inParallel(isParallel)
        .fromMCMulticastRoute()
        .add();

    new FromDirectOutConnectorRouteTemplate(useCase, suffix)
        .fromCustomRouteBuilder(outConnectors);
  }
}
