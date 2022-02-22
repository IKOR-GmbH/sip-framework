package de.ikor.sip.foundation.core.proxies;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@ConditionalOnProperty(value = "sip.core.proxy.enabled", havingValue = "true")
public class ProxyAutoConfig {}
