package de.ikor.sip.foundation.soap.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import org.apache.camel.builder.endpoint.dsl.CxfEndpointBuilderFactory.CxfEndpointBuilder;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.junit.jupiter.api.Test;

class SOAPEndpointBuilderTest {

  @Test
  void WHEN_beanNotExistsInRegistry_THEN_CamelEndpointIsCreated() {

    // arrange
    Map<String, CxfEndpoint> cxfBeans = Collections.emptyMap();

    // act
    CxfEndpointBuilder cxfEndpointBuilder =
        SOAPEndpointBuilder.generateCXFEndpoint(
            cxfBeans, "serviceClassName", "serviceClassQualifiedName", "address");

    // assert
    assertThat(cxfEndpointBuilder.getUri())
        .isEqualTo("cxf://address?dataFormat=PAYLOAD&serviceClass=serviceClassQualifiedName");
  }
}
