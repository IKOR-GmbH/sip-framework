package de.ikor.sip.foundation.core.framework;

import de.ikor.sip.foundation.core.framework.routers.RouteStarter;
import de.ikor.sip.foundation.core.framework.templates.FromCentralRouterTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromDirectOutConnectorRouteTemplate;
import org.springframework.context.annotation.Import;

@Import({ // TODO load whole package
  RouteStarter.class,
  FromDirectOutConnectorRouteTemplate.Template.class,
  FromCentralRouterTemplate.Template.class
})
public class FrameworkAutoConfig {}
