package de.ikor.sip.foundation.core.actuator.health.camel;

import org.apache.camel.DeferredContextBinding;
import org.apache.camel.health.HealthCheck;
import org.apache.camel.impl.health.RouteHealthCheck;
import org.apache.camel.impl.health.RoutesHealthCheckRepository;

import java.util.stream.Stream;

@DeferredContextBinding
public class SIPHealthCheckRepositoryRoutes extends RoutesHealthCheckRepository {

  public SIPHealthCheckRepositoryRoutes() {
    super();
  }

  @Override
  public Stream<HealthCheck> stream() {

    return super.stream()
        .filter(check -> check instanceof RouteHealthCheck)
        .map(c -> (RouteHealthCheck) c)
        .map(SIPHealthCheckRoute::new);
  }
}
