package de.ikor.sip.testframework.util;


import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SIPEndpointResolverTest {

    private static final String CONNECTION_ALIAS = "alias";
    public static final String ENDPOINT_URI = "endpointuri";
    public static final String METHOD = "POST";

    SIPEndpointResolver subject;
    Exchange exchange;
    ServletRegistrationBean servletRegistrationBean;
    CamelContext camelContext;

    @BeforeEach
    void setup() {
        exchange = mock(Exchange.class);
        servletRegistrationBean = mock(ServletRegistrationBean.class);
        camelContext = mock(CamelContext.class);
        subject = new SIPEndpointResolver(servletRegistrationBean, camelContext);
    }

    @Test
    void When_resolveURI_With_NoRest_Expect_resolvedURI() {
        // arrange
        Route route = mock(Route.class);
        Endpoint endpoint = mock(Endpoint.class);
        when(exchange.getProperty("connectionAlias", String.class)).thenReturn(CONNECTION_ALIAS);
        when(camelContext.getRoute(CONNECTION_ALIAS)).thenReturn(route);
        when(route.getEndpoint()).thenReturn(endpoint);
        when(endpoint.getEndpointUri()).thenReturn(ENDPOINT_URI);

        // act
        String resolvedURI = subject.resolveURI(exchange);

        // assert
        assertThat(resolvedURI).isEqualTo(ENDPOINT_URI);
    }

    @Test
    void When_resolveURI_With_Rest_Expect_resolvedURI() {
        // arrange
        Route route = mock(Route.class);
        RestEndpoint endpoint = mock(RestEndpoint.class);
        when(exchange.getProperty("connectionAlias", String.class)).thenReturn(CONNECTION_ALIAS);
        when(camelContext.getRoute(CONNECTION_ALIAS)).thenReturn(route);
        when(route.getEndpoint()).thenReturn(endpoint);
        when(endpoint.getPath()).thenReturn(ENDPOINT_URI);
        when(endpoint.getMethod()).thenReturn(METHOD);
        List<String> mappings = new ArrayList<>();
        mappings.add("/*");
        when(servletRegistrationBean.getUrlMappings()).thenReturn(mappings);
        String expectedURI = "rest:"+ METHOD + ":" + ENDPOINT_URI;

        // act
        String resolvedURI = subject.resolveURI(exchange);

        // assert
        assertThat(resolvedURI).isEqualTo(expectedURI);
    }
}