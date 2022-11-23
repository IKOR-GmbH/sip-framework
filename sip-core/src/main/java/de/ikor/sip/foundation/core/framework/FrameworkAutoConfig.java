package de.ikor.sip.foundation.core.framework;

import de.ikor.sip.foundation.core.framework.beans.*;
import de.ikor.sip.foundation.core.framework.routers.RouteStarter;
import de.ikor.sip.foundation.core.framework.templates.FromCentralRouterTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromDirectOutConnectorRouteTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplate;
import org.springframework.context.annotation.Import;

@Import({ // TODO load whole package
  RouteStarter.class,
  FromMiddleComponentRouteTemplate.Template.class,
  FromDirectOutConnectorRouteTemplate.Template.class,
  FromCentralRouterTemplate.Template.class,
  ExchangeScopeConfig.class,
  CDMHolder.class,
        CDMValueSetter.class,
        ExchangeEventNotifier.class,
        CDMRepository.class
})
public class FrameworkAutoConfig {}
