package de.ikor.sip.foundation.core.openapi;

import org.apache.camel.springboot.openapi.OpenApiAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;

/** Class in charge of toggling all beans under de.ikor.sip.foundation.core.openapi package */
@ComponentScan
@ConditionalOnClass(OpenApiAutoConfiguration.class)
public class OpenApiResolverAutoConfig {}
