package de.ikor.sip.foundation.core.declarative.orchestration.process.routebuilding;

import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessOrchestrationHandlers;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.CallNestedCondition;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.CallProcessConsumerBase;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.CallableWithinProcessDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.RouteGeneratorHelper;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.model.ProcessorDefinition;

/**
 * Class for generating Camel routes for process consumer calls from a DSL
 *
 * <p><em>For internal use only</em>
 */
@Slf4j
@SuppressWarnings("rawtypes")
final class RouteGeneratorForCallConditionalProcessConsumer extends RouteGeneratorProcessBase {

  private final CallNestedCondition<?> conditionalDefinition;

  private final Set<IntegrationScenarioDefinition> overallUnhandledConsumers;

  RouteGeneratorForCallConditionalProcessConsumer(
      final CompositeOrchestrationInfo orchestrationInfo,
      final CallNestedCondition conditionalDefinition,
      final Set<IntegrationScenarioDefinition> overallUnhandledConsumers) {
    super(orchestrationInfo);
    this.conditionalDefinition = conditionalDefinition;
    this.overallUnhandledConsumers = overallUnhandledConsumers;
  }

  private IntegrationScenarioDefinition retrieveConsumerFromClassDefinition(
      final CallProcessConsumerBase element) {
    return getConsumers().stream()
        .filter(
            consumer -> RouteGeneratorHelper.getConsumerClass(element).equals(consumer.getClass()))
        .findFirst()
        .orElseThrow(
            () ->
                SIPFrameworkInitializationException.init(
                    "Consumer-class '%s' is used on orchestration for process '%s', but it is not registered with that scenario. Registered outbound connector classes are %s",
                    RouteGeneratorHelper.getConsumerClass(element).getName(),
                    getCompositeId(),
                    getConsumers().stream().map(conn -> conn.getClass().getName()).toList()));
  }

  private IntegrationScenarioDefinition retrieveConsumerFromClassDefinition(
      final CallNestedCondition element) {
    return getConsumers().stream()
        .filter(
            consumer -> RouteGeneratorHelper.getConsumerClass(element).equals(consumer.getClass()))
        .findFirst()
        .orElseThrow(
            () ->
                SIPFrameworkInitializationException.init(
                    "Consumer-class '%s' is used on orchestration for process '%s', but it is not registered with that scenario. Registered outbound connector classes are %s",
                    RouteGeneratorHelper.getConsumerClass(element).getName(),
                    getCompositeId(),
                    getConsumers().stream().map(conn -> conn.getClass().getName()).toList()));
  }

  private List<IntegrationScenarioDefinition> getConsumers() {
    return getDeclarationsRegistry().getCompositeProcessConsumerDefinitions(getCompositeId());
  }

  <T extends ProcessorDefinition<T>> void generateRoute(final T routeDefinition) {
    List<CallNestedCondition.ProcessBranchStatements> conditionalStatements =
        RouteGeneratorHelper.getConditionalStatements(conditionalDefinition);
    if (conditionalStatements.isEmpty()) {
      SIPFrameworkInitializationException.init(
          "Empty conditional statement attached in orchestration for integration-scenario %s");
    }

    final var choiceDef = routeDefinition.choice();
    for (final var branch : conditionalStatements) {
      if (branch.statements().isEmpty()) {
        var branchIndex = conditionalStatements.indexOf(branch) + 1;
        log.warn(
            "Orchestration for integration-scenario {} contains a conditional-statement that does not specify any actions in branch #{}",
            branchIndex);
      }
      choiceDef.when(
          exchange ->
              CompositeProcessOrchestrationHandlers.handleConditional(
                  exchange,
                  RouteGeneratorHelper.getStepResultCloner(conditionalDefinition),
                  Optional.ofNullable(branch.predicate())));
      branch
          .statements()
          .forEach(
              statement -> {
                buildRouteForStatement(choiceDef, (CallableWithinProcessDefinition) statement);
              });
      choiceDef.endChoice();
    }
    List<CallableWithinProcessDefinition> unconditionalStatements =
        RouteGeneratorHelper.getUnonditionalStatements(conditionalDefinition);

    if (!unconditionalStatements.isEmpty()) {
      choiceDef.otherwise();
      unconditionalStatements.forEach(statement -> buildRouteForStatement(choiceDef, statement));
      choiceDef.endChoice();
    }

    choiceDef.end();
  }

  private <T extends ProcessorDefinition<T>> void buildRouteForStatement(
      final T routeDefinition, final CallableWithinProcessDefinition statement) {
    if (statement instanceof CallProcessConsumerBase callDef) {
      new RouteGeneratorForCallProcessConsumer(
              getOrchestrationInfo(), callDef, overallUnhandledConsumers)
          .generateRoute(routeDefinition);
    } else {
      throw SIPFrameworkInitializationException.init(
          "Unhandled statement type '%s' used in conditional-branch of orchestration for integration-scenario %s");
    }
  }
}
