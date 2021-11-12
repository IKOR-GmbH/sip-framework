package de.ikor.sip.foundation.security.config;

import de.ikor.sip.foundation.security.authentication.CompositeAuthenticationFilter;
import de.ikor.sip.foundation.security.authentication.SIPAuthenticationProvider;
import de.ikor.sip.foundation.security.authentication.common.extractors.TokenExtractors;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties.AuthProviderSettings;
import java.util.List;
import java.util.stream.Collectors;
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
@ConditionalOnSIPSecurityAuthenticationEnabled
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private List<SIPAuthenticationProvider<?>> authProviders;
  @Autowired private SecurityConfigProperties config;
  @Autowired private TokenExtractors tokenExtractors;

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    List<Class<?>> existingAuthProviderClasses =
        authProviders.stream().map(Object::getClass).collect(Collectors.toList());
    List<Class<?>> missingAuthProviders =
        config.getAuthProviders().stream()
            .map(AuthProviderSettings::getClassname)
            .filter(a -> !existingAuthProviderClasses.contains(a))
            .collect(Collectors.toList());

    if (!missingAuthProviders.isEmpty()) {
      throw new IllegalStateException(
          "Some configured auth providers are not available in code: " + missingAuthProviders);
    }

    if (config.getAuthProviders().size()
        > config.getAuthProviders().stream()
            .map(AuthProviderSettings::getClassname)
            .distinct()
            .count()) {
      throw new IllegalStateException(
          "Each auth provider may only be configured once, duplicates are not allowed");
    }

    authProviders.stream()
        .filter(
            a ->
                config.getAuthProviders().stream()
                    .map(AuthProviderSettings::getClassname)
                    .anyMatch(n -> n.equals(a.getClass())))
        .forEach(authManagerBuilder::authenticationProvider);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // disable sessions completely
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // add our composite authentication filter for all requests (besides the ones ignored separately
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

  @Override
  public void configure(WebSecurity web) {
    final WebSecurity.IgnoredRequestConfigurer ignoredRequestConfigurer = web.ignoring();
    config.getIgnoredEndpoints().forEach(ignoredRequestConfigurer::antMatchers);
  }
}
