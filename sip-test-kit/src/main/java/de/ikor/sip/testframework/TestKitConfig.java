package de.ikor.sip.testframework;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@ConditionalOnProperty(value = "sip.core.testkit.enabled", havingValue = "true")
public class TestKitConfig {}
