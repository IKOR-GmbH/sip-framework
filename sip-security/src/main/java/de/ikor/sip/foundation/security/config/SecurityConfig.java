package de.ikor.sip.foundation.security.config;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.security.authentication.CompositeAuthenticationFilter;
import de.ikor.sip.foundation.security.authentication.SIPAuthenticationProvider;
import de.ikor.sip.foundation.security.authentication.common.extractors.TokenExtractors;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties.AuthProviderSettings;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Central place of all config related stuff for spring-security settings regarding the sip
 * authentication features
 *
 * @author thomas.stieglmaier
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final List<SIPAuthenticationProvider<?>> authProviders;

  private final SecurityConfigProperties config;

  private final TokenExtractors tokenExtractors;

  /**
   * Autowired constructor for creating SIP Security Configuration
   *
   * @param authProviders (optional) list of auth providers defined in the config
   * @param config SIP Security config
   * @param tokenExtractors (optional) registered token extractors filled by authProviders
   */
  @Autowired
  public SecurityConfig(
      Optional<List<SIPAuthenticationProvider<?>>> authProviders,
      SecurityConfigProperties config,
      Optional<TokenExtractors> tokenExtractors) {
    super();
    this.authProviders = authProviders.orElse(Collections.emptyList());
    this.config = config;
    this.tokenExtractors = tokenExtractors.orElse(null);
  }

  /** Register Spring-security provided authenticationManager as a @Bean */
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder authManagerBuilder)
      throws IllegalStateException {
    List<Class<?>> autowiredAuthProviders =
        List.copyOf(authProviders.stream().map(Object::getClass).toList());

    List<Class<?>> providersUnavailableAtRuntime =
        List.copyOf(
            config.getAuthProviders().stream()
                .map(AuthProviderSettings::getClassname)
                .filter(a -> !autowiredAuthProviders.contains(a))
                .toList());

    if (!providersUnavailableAtRuntime.isEmpty()) {
      throw SIPFrameworkException.init(
          "Some providers declared in the config are not available in runtime: %s",
          providersUnavailableAtRuntime);
    }

    if (configHasDuplicateAuthProviders()) {
      throw new SIPFrameworkException(
          "Each auth provider may only be configured once, duplicates are not allowed");
    }

    // Register all auth providers that exist in the config
    authProviders.stream()
        .filter(
            a ->
                config.getAuthProviders().stream()
                    .map(AuthProviderSettings::getClassname)
                    .anyMatch(n -> n.equals(a.getClass())))
        .forEach(authManagerBuilder::authenticationProvider);
  }

  private boolean configHasDuplicateAuthProviders() {
    return config.getAuthProviders().size()
        > config.getAuthProviders().stream()
            .map(AuthProviderSettings::getClassname)
            .distinct()
            .count();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // disable sessions completely
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // add our composite authentication Filter for all requests (besides the ones ignored separately
    // in the WebSecurity configure method
    http.addFilterAt(
            new CompositeAuthenticationFilter(tokenExtractors, config, authenticationManagerBean()),
            BasicAuthenticationFilter.class)
        .authorizeRequests()
        .anyRequest()
        .authenticated();

    if (config.isDisableCsrf()) {
      http.csrf().disable();
    }
  }

  /** Set globally ignored endpoints from config */
  @Override
  public void configure(WebSecurity web) {
    final WebSecurity.IgnoredRequestConfigurer ignoredRequestConfigurer = web.ignoring();
    config.getIgnoredEndpoints().forEach(ignoredRequestConfigurer::antMatchers);
  }
}
