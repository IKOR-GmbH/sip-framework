package de.ikor.sip.foundation.testkit;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SIPFeature(name = "testkit")
@ConditionalOnProperty(value = "sip.testkit.enabled", havingValue = "true")
public class TestKitConfig {}
