package de.ikor.sip.foundation.core.actuator.routes;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

/**
 * Entry point of the HTTP-only Actuator endpoint that exposes management functions from the
 * CamelContext and Camel's JMX MBeans.
 *
 * <p>Among other features, you can use it to list, start and stop Camel routes, as well as to get a
 * plenty of details about each one of them.
 */
@Component
@RestControllerEndpoint(id = "adapter-routes")
public class MarkdownFileEndpoint implements InfoContributor {

  @Override
  public void contribute(Info.Builder builder) {
    Map<String, Integer> userDetails = new HashMap<>();

    builder.withDetail("users", userDetails);
  }
}
