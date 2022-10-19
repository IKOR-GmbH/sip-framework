package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.OutConnector;
import de.ikor.sip.foundation.core.framework.OutEndpoint;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;

public class ComplexOutConnector extends OutConnector {

  public static final String DIRECT_COMPLEX_MCAST_1_URI = "direct:complex-mcast-1";
  public static final String DIRECT_COMPLEX_MCAST_2_URI = "direct:complex-mcast-2";
  public static RoutesBuilder helperRouteBuilder =
      new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          from(DIRECT_COMPLEX_MCAST_1_URI)
              .process(exchange -> System.out.println())
              .setBody(simple("body 1"));
          from(DIRECT_COMPLEX_MCAST_2_URI)
              .process(exchange -> System.out.println())
              .setBody(body().append(simple("body 2")));
        }
      };

  @Override
  public void configure(RouteDefinition route) {
    route
        .multicast(aggregationStrategy())
        .to(OutEndpoint.instance(DIRECT_COMPLEX_MCAST_1_URI, "complex-mcast-1"))
        .to(OutEndpoint.instance(DIRECT_COMPLEX_MCAST_2_URI, "complex-mcast-2"));
  }

  private AggregationStrategy aggregationStrategy() {
    return new GroupedBodyAggregationStrategy();
  }
}
