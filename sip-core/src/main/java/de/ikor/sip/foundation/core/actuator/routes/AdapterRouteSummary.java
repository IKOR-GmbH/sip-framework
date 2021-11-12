package de.ikor.sip.foundation.core.actuator.routes;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.camel.api.management.mbean.ManagedRouteMBean;

/** Provides a brief summary for the Camel Route. */
@Getter
@Setter
public class AdapterRouteSummary {
  private String id;
  private String state;
  private long exchangesTotal;
  private long exchangesCompleted;
  private long exchangesFailed;
  private long exchangesInflight;

  /**
   * Initializes an AdapterRouteSummary
   *
   * @param managedRoute {@link ManagedRouteMBean}
   */
  @SneakyThrows
  public AdapterRouteSummary(ManagedRouteMBean managedRoute) {
    this.id = managedRoute.getRouteId();
    this.state = managedRoute.getState();
    this.exchangesTotal = managedRoute.getExchangesTotal();
    this.exchangesCompleted = managedRoute.getExchangesCompleted();
    this.exchangesFailed = managedRoute.getExchangesFailed();
    this.exchangesInflight = managedRoute.getExchangesInflight();
  }
}
