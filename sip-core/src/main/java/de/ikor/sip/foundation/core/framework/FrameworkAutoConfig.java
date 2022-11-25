package de.ikor.sip.foundation.core.framework;

import de.ikor.sip.foundation.core.framework.beans.*;
import de.ikor.sip.foundation.core.framework.routers.RouteStarter;
import org.springframework.context.annotation.Import;

@Import({RouteStarter.class,
        ConversationScopeConfig.class,
        CDMHolder.class,
        CDMValueSetter.class,
        ExchangeEventNotifier.class,
        CDMRepository.class})// TODO load whole package
public class FrameworkAutoConfig {}
