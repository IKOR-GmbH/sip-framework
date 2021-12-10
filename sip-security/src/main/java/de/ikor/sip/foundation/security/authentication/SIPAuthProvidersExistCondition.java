package de.ikor.sip.foundation.security.authentication;

import de.ikor.sip.foundation.security.config.SecurityConfigProperties.AuthProviderSettings;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/** Condition class matching whether authentication providers are defined */
public class SIPAuthProvidersExistCondition extends SpringBootCondition {
  private static final Bindable<List<AuthProviderSettings>> PROVIDER_LIST =
      Bindable.listOf(AuthProviderSettings.class);

  @Override
  public ConditionOutcome getMatchOutcome(
      ConditionContext context, AnnotatedTypeMetadata metadata) {

    String listProperty = "sip.security.authentication.auth-providers";

    BindResult<List<AuthProviderSettings>> property =
        Binder.get(context.getEnvironment()).bind(listProperty, PROVIDER_LIST);
    List<AuthProviderSettings> authSettings = property.orElse(null);

    if (authSettings != null && !authSettings.isEmpty()) {
      return ConditionOutcome.match();
    }

    return ConditionOutcome.noMatch(
        ConditionMessage.forCondition("SIP auth provider condition")
            .didNotFind("property")
            .items(listProperty));
  }
}
