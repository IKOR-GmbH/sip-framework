package de.ikor.sip.foundation.core.framework;

import de.ikor.sip.foundation.core.framework.routers.RouteStarter;
import org.springframework.context.annotation.Import;

@Import(RouteStarter.class)// TODO load whole package
public class FrameworkAutoConfig {}
