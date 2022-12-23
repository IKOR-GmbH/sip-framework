package de.ikor.sip.foundation.core.framework;

import de.ikor.sip.foundation.core.framework.endpoints.AddEndpointDomainValidateAndTransformInterceptStrategy;
import de.ikor.sip.foundation.core.framework.routers.RouteStarter;
import de.ikor.sip.foundation.core.framework.scope.conversation.ConversationScopeBean;
import de.ikor.sip.foundation.core.framework.scope.conversation.notifiers.ConversationCompletedExchangeEventNotifier;
import de.ikor.sip.foundation.core.framework.scope.conversation.notifiers.ConversationCreatedExchangeEventNotifier;
import de.ikor.sip.foundation.core.framework.scope.exchange.ExchangeScopeBean;
import de.ikor.sip.foundation.core.framework.scope.CustomScopeConfig;
import de.ikor.sip.foundation.core.framework.scope.exchange.notifiers.CompletedExchangeEventNotifier;
import de.ikor.sip.foundation.core.framework.scope.exchange.notifiers.CreatedExchangeEventNotifier;
import org.springframework.context.annotation.Import;

@Import({
  RouteStarter.class,
  CustomScopeConfig.class,
  ExchangeScopeBean.class,
  ConversationScopeBean.class,
  ConversationCreatedExchangeEventNotifier.class,
  ConversationCompletedExchangeEventNotifier.class,
  CompletedExchangeEventNotifier.class,
  CreatedExchangeEventNotifier.class,
  AddEndpointDomainValidateAndTransformInterceptStrategy.class
}) // TODO load whole package
public class FrameworkAutoConfig {}
