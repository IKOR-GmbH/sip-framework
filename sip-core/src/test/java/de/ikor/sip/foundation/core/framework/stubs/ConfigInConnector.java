package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConfigInConnector extends InConnector {
  private String channel = "config";

  @Override
  public String getName() {
    return channel + " conn";
  }

  public static ConfigInConnector withSedaChannel(String channel) {
    return new ConfigInConnector(channel);
  }

  @Override
  public void configure() {
    from(InEndpoint.instance("seda:" + channel, channel + "-id"))
        .process(exchange -> exchange.getMessage());
  }

  private ConfigInConnector(String channel) {
    this.channel = channel;
  }
}
