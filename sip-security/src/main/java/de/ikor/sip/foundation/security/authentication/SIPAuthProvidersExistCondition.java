package de.ikor.sip.foundation.security.authentication;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

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
    ConditionOutcome outcome = ConditionOutcome.match();
    String listPropertyName = "sip.security.authentication.auth-providers";

    if (isEmpty(getAuthProviderSettingsList(context, listPropertyName))) {
      outcome =
          ConditionOutcome.noMatch(
              ConditionMessage.forCondition("SIP auth providers condition")
                  .didNotFind("property")
                  .items(listPropertyName));
    }
    return outcome;
  }

  private List<AuthProviderSettings> getAuthProviderSettingsList(
      ConditionContext context, String listPropertyName) {
    BindResult<List<AuthProviderSettings>> bindResult =
        Binder.get(context.getEnvironment()).bind(listPropertyName, PROVIDER_LIST);
    return bindResult.orElse(null);
  }
}
