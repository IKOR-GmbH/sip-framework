package de.ikor.sip.testkit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@ConditionalOnProperty(value = "sip.testkit.enabled", havingValue = "true")
public class TestKitConfig {}
