package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.beans.CDMHolder;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScopeBeanInConnector extends InConnector {
  @Autowired
  CDMHolder bean;

  @Override
  public String getName() {
    return "bean-connector";
  }

  @Override
  public void configure() {
    from(rest("/hello-bean", "get-bean").get())
        .process(exchange -> {
          bean.setInternal("hello bean");
        });
  }

  @Override
  public void configureOnException() {}
}
