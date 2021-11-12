package de.ikor.sip.foundation.security.authentication;

import de.ikor.sip.foundation.security.config.SecurityConfigProperties.AuthProviderSettings;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

/**
 * Matcher class implementing the logic for the {@link ConditionalOnSIPAuthProvider} annotation.
 * Look there for details on the possible configuration.
 *
 * @author thomas.stieglmaier
 */
public class SIPAuthProviderCondition extends SpringBootCondition {
  private static final Bindable<List<AuthProviderSettings>> PROVIDER_LIST =
      Bindable.listOf(AuthProviderSettings.class);

  @Override
  public ConditionOutcome getMatchOutcome(
      ConditionContext context, AnnotatedTypeMetadata metadata) {
    MultiValueMap<String, Object> attributes =
        metadata.getAllAnnotationAttributes(ConditionalOnSIPAuthProvider.class.getName());

    String listProperty = "sip.security.authentication.auth-providers";

    BindResult<List<AuthProviderSettings>> property =
        Binder.get(context.getEnvironment()).bind(listProperty, PROVIDER_LIST);
    List<AuthProviderSettings> authSettings = property.orElse(null);

    if (authSettings != null) {
      Class<?> listItemValue = (Class<?>) attributes.get("listItemValue").get(0);
      Class<?> validationClass = (Class<?>) attributes.get("validationClass").get(0);

      Optional<AuthProviderSettings> authProviderOpt =
          authSettings.stream().filter(s -> s.getClassname().equals(listItemValue)).findFirst();
      if (authProviderOpt.isPresent()) {
        AuthProviderSettings authProvider = authProviderOpt.get();

        if (!validationClass.equals(Object.class)
            && !authProvider.getValidation().getClassname().equals(validationClass)) {
          return ConditionOutcome.noMatch(
              ConditionMessage.forCondition("SIP validation type check")
                  .didNotFind("property")
                  .items(listProperty));
        }
        return ConditionOutcome.match();
      }
    }

    return ConditionOutcome.noMatch(
        ConditionMessage.forCondition("SIP list location")
            .didNotFind("property")
            .items(listProperty));
  }
}
