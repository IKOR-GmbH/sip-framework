package de.ikor.sip.foundation.core.declarative;

import java.util.function.Consumer;
import org.apache.camel.model.RouteDefinition;

@FunctionalInterface
public interface RouteDefinitionConsumer extends Consumer<RouteDefinition> {}
