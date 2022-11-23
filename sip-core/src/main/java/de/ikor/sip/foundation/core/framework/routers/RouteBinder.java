package de.ikor.sip.foundation.core.framework.routers;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import de.ikor.sip.foundation.core.framework.templates.FromDirectOutConnectorRouteTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;

@RequiredArgsConstructor
public class RouteBinder {
  private final String useCase;
  private final Class<?> centralModelRequest;
  protected String suffix = EMPTY;
  @Getter protected final List<RouteBuilder> outConnectorsBuilders = new ArrayList<>();

  public RouteBinder(String useCase, Class<?> centralModelRequest, String suffix) {
    this.useCase = useCase;
    this.centralModelRequest = centralModelRequest;
    this.suffix = suffix;
  }

  protected void appendOutConnectorsSeq(OutConnectorDefinition[] outConnectors) {
    appendConnectors(outConnectors, false);
  }

  public void appendOutConnectorsParallel(OutConnectorDefinition[] outConnectors) {
    appendConnectors(outConnectors, true);
  }

  private void appendConnectors(OutConnectorDefinition[] outConnectors, boolean isParallel) {
    FromMiddleComponentRouteTemplate.withUseCase(useCase)
        .withSuffix(suffix)
        .withCentralDomainRequest(centralModelRequest)
        .outConnectors(outConnectors)
        .inParallel(isParallel)
        .fromMCMulticastRoute()
        .add();

    new FromDirectOutConnectorRouteTemplate(useCase, suffix).fromCustomRouteBuilder(outConnectors);
  }
}
