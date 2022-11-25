package de.ikor.sip.foundation.core.framework;

import de.ikor.sip.foundation.core.framework.endpoints.AddEndpointDomainValidateAndTransformInterceptStrategy;
import de.ikor.sip.foundation.core.framework.routers.RouteStarter;
import org.springframework.context.annotation.Import;

@Import({RouteStarter.class, AddEndpointDomainValidateAndTransformInterceptStrategy.class})// TODO load whole package
public class FrameworkAutoConfig {}
