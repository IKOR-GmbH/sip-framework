package de.ikor.sip.foundation.core.actuator.health.camel;

import org.apache.camel.DeferredContextBinding;
import org.apache.camel.health.HealthCheck;
import org.apache.camel.impl.health.ConsumerHealthCheck;
import org.apache.camel.impl.health.ConsumersHealthCheckRepository;

import java.util.stream.Stream;

@DeferredContextBinding
public class SIPHealthCheckRepositoryConsumers extends ConsumersHealthCheckRepository {

  public SIPHealthCheckRepositoryConsumers() {
    super();
  }

  @Override
  public Stream<HealthCheck> stream() {
    return super.stream()
        .filter(check -> check instanceof ConsumerHealthCheck)
        .map(c -> (ConsumerHealthCheck) c)
        .map(SIPHealthCheckConsumer::new);
  }
}
