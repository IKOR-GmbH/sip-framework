package de.ikor.sip.foundation.camel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import org.apache.camel.Endpoint;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SipMiddleComponentTest {

  private static final String REMAINING_URI_PART = "foo";
  private static final String CONSUMER_URI = "sipmc:" + REMAINING_URI_PART;

  @Mock private SpringBootCamelContext context;
  private SipMiddleComponent subject;
  private List<RouteDefinition> rdList = new LinkedList<>();

  @BeforeEach
  void setUp() {
    RouteDefinition rd = createRouteDefinition(CONSUMER_URI);
    rdList.add(rd);
    when(context.getRouteDefinitions()).thenReturn(rdList);
    subject = new SipMiddleComponent(context);
  }

  @Test
  void WHEN_createEndpoint_WITH_singleConsumer_THEN_noMultiConsumers() throws Exception {
    // arrange

    // act
    Endpoint result = subject.createEndpoint(CONSUMER_URI, REMAINING_URI_PART, null);

    // assert
    assertThat(result).isInstanceOf(SipMiddleEndpoint.class);
    verify(context).getEndpoint("seda:" + REMAINING_URI_PART + "?multipleConsumers=false");
  }

  @Test
  void WHEN_createEndpoint_WITH_manyConsumers_THEN_multiConsumers() throws Exception {
    // arrange
    rdList.add(createRouteDefinition(CONSUMER_URI));

    // act
    Endpoint result = subject.createEndpoint(CONSUMER_URI, REMAINING_URI_PART, null);

    // assert
    assertThat(result).isInstanceOf(SipMiddleEndpoint.class);
    verify(context).getEndpoint("seda:" + REMAINING_URI_PART + "?multipleConsumers=true");
  }

  private RouteDefinition createRouteDefinition(String componentUri) {
    RouteDefinition rd = new RouteDefinition();
    FromDefinition fromDef = new FromDefinition();
    fromDef.setUri(componentUri);
    rd.setInput(fromDef);
    return rd;
  }
}
