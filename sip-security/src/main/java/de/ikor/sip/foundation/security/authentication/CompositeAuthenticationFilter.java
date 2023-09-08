package de.ikor.sip.foundation.security.authentication;

import de.ikor.sip.foundation.security.authentication.common.extractors.TokenExtractors;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties.AuthProviderSettings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * The authentication filter which handles composite authentication, which means that in contrast to
 * default spring-security (if one authentication succeeds, the user is "logged in") all configured
 * sip authentication providers responsible for a specific endpoint need to succeed with the
 * authentication. If only one of them fails, the whole request will be seen as Unauthorized.
 *
 * @author thomas.stieglmaier
 */
@Slf4j
@AllArgsConstructor
public class CompositeAuthenticationFilter extends GenericFilterBean {

  private final TokenExtractors tokenExtractors;
  private final SecurityConfigProperties config;
  private final AuthenticationManager authManager;

  public final void doFilter(
      ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    System.out.println(request);
    if (request instanceof HttpServletRequest httpRequest) {
      if (response instanceof HttpServletResponse httpResponse) {
        String urlPath = httpRequest.getRequestURI();
        List<SIPAuthenticationToken<?>> authenticatedTokens = new ArrayList<>();

        for (AuthProviderSettings aps : config.getAuthProviders()) {
          if (aps.isResponsibleFor(urlPath)) {
            try {
              Authentication token =
                  tokenExtractors.extractTokenFor(aps.getClassname(), httpRequest);
              authenticatedTokens.add((SIPAuthenticationToken<?>) authManager.authenticate(token));

            } catch (BadCredentialsException e) {
              SecurityContextHolder.clearContext();
              httpResponse.setStatus(401);
              return;

            } catch (AuthenticationException e) {
              SecurityContextHolder.clearContext();
              httpResponse.setStatus(403);
              return;

            } catch (Exception e) {
              log.info("sip.security.requestautherror_{}", e);
              SecurityContextHolder.clearContext();
              httpResponse.setStatus(500);
              return;
            }
          }
        }

        // if no auth-provider was responsible this means that the requested url doesn't need
        // authentication, in our configuration we require every endpoint to be authenticated,
        // so we also set the security context to be authenticated in those cases (even though
        // no inner auth tokens exist, but this is fine)
        SecurityContextHolder.getContext()
            .setAuthentication(new CompositeAuthenticationToken(authenticatedTokens));
      }
    }
    filterChain.doFilter(request, response);
  }
}
