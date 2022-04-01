package de.ikor.sip.foundation.core.actuator.health.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Endpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;

/**
 * {@link HttpHealthIndicators} contains several functions that implement health checks for HTTP
 * endpoints.
 */
@Slf4j
public class HttpHealthIndicators {
  private static final int DEFAULT_HTTP_TIMEOUT = 30000;

  private HttpHealthIndicators() {}

  /**
   * A dummy health function that always returns health status UP.
   *
   * @return {@link Health}
   */
  public static Health alwaysHealthy() {
    return new Health.Builder().up().build();
  }

  /**
   * Returns {@link Health} information based on the the URL from the Endpoint.
   *
   * @param endpoint Endpoint singleton
   * @return Returns health status UNKNOWN for every detected endpoint if not further specified
   */
  public static Health alwaysUnknown(Endpoint endpoint) {
    return new Health.Builder().withDetail("url", endpoint.getEndpointKey()).unknown().build();
  }

  /**
   * Returns {@link Health} information based on the ability to perform GET HTTP request using the
   * URL from the Endpoint.
   *
   * @param endpoint Endpoint singleton
   * @return Returns health status UP if it successfully execute the request, UNKNOWN if HTTP
   *     response code is not 2xx, or DOWN if request fails for any other reason.
   */
  public static Health urlHealthIndicator(Endpoint endpoint) {
    return urlHealthStatus(endpoint.getEndpointKey(), DEFAULT_HTTP_TIMEOUT);
  }

  /**
   * Returns health checking function using custom url along with desired timeout.
   *
   * @param url The URL to use when performing the health check
   * @param timeout the timeout
   * @return function that checks health of the HTTP endpoint
   */
  public static Function<Endpoint, Health> urlHealthIndicator(String url, int timeout) {
    return endpoint -> urlHealthStatus(url, timeout);
  }

  private static Health urlHealthStatus(String url, int timeout) {
    Health.Builder builder = new Health.Builder();
    builder.withDetail("url", url);
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setConnectTimeout(timeout);
      connection.connect();
      Optional<HttpStatus> httpStatus =
          Optional.ofNullable(HttpStatus.resolve(connection.getResponseCode()));

      httpStatus.ifPresentOrElse(
          status -> {
            setBuilderStateFromHTTPStatus(builder, status);
          },
          builder::unknown);
    } catch (IOException e) {
      builder.down(e);
    }
    return builder.build();
  }

  private static void setBuilderStateFromHTTPStatus(Health.Builder builder, HttpStatus status) {
    builder.withDetail("status", status).withDetail("statusCode", status.value());

    if (status.is2xxSuccessful() || status.value() == HttpStatus.METHOD_NOT_ALLOWED.value()) {
      builder.up();
      return;
    }

    if (status.is5xxServerError()) {
      builder.down();
      return;
    }

    builder.unknown();
  }
}
