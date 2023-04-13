package de.ikor.sip.foundation.core.declarative.annonation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used to disable:
 *
 * <ul>
 *   <li>{@link de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition}s
 *   <li>{@link de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition}s,
 *       which also disables all consumers and providers attached to the scenario
 *   <li>{@link de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition}s,
 *       which also disables all connectors attached to it *
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Disabled {}
