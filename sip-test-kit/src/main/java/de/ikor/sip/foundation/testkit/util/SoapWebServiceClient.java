package de.ikor.sip.foundation.testkit.util;

import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoapWebServiceClient {

    private static final String TEST_MODE_HEADER_KEY = "test-mode";
    private static final String TEST_NAME_HEADER_KEY = "test-name";
    private static final String CONTEXT_PATH_PREFIX = "[/]";
    private static final String CONTEXT_PATH_SUFFIX = "[/]$";

    private final CamelContext camelContext;

    @Value("${sip.adapter.camel-cxf-endpoint-context-path}")
    private String cxfContextPath = "";

    @Autowired
    Environment environment;

    public Exchange postSOAPXML(Exchange exchange, CxfEndpoint cxfEndpoint) {
        String resp = null;
        try {

            String soapBody = exchange.getMessage().getBody(String.class);

            HttpClient httpclient = HttpClientBuilder.create().build();
            StringEntity strEntity = new StringEntity(soapBody, "UTF-8");
            // URL of request
            String address = trimAddressPrefix(cxfEndpoint.getAddress());
            HttpPost post = new HttpPost("http://localhost:" + environment.getProperty("local.server.port") + trimAddressSuffix(cxfContextPath) + "/" + address);
            post.setHeader(TEST_MODE_HEADER_KEY, exchange.getMessage().getHeader(TEST_MODE_HEADER_KEY, String.class));
            post.setHeader(TEST_NAME_HEADER_KEY, exchange.getMessage().getHeader(TEST_NAME_HEADER_KEY, String.class));
            post.setEntity(strEntity);

            // Execute request
            HttpResponse response = httpclient.execute(post);
            HttpEntity respEntity = response.getEntity();
            Header[] headers = response.getAllHeaders();


            if (respEntity != null) {
                resp = EntityUtils.toString(respEntity);
                System.err.println("Response is: " + resp);
            } else {
                System.err.println("No Response");
            }
        } catch (Exception e) {
            System.err.println("WebService SOAP exception = " + e.toString());
        }

        ExchangeBuilder exchangeBuilder =
                ExchangeBuilder.anExchange(camelContext).withBody(resp);
//        properties.getMessage().getHeaders().forEach(exchangeBuilder::withHeader);
//        exchangeBuilder.withProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, properties.getEndpoint());
        return exchangeBuilder.build();
    }

    // WIP - Consider implementing this on other place
    private String trimAddressPrefix(String address) {
        String res = address.replaceFirst(CONTEXT_PATH_PREFIX, "");
        System.out.println(res);
        return res;
    }

    // WIP - Consider implementing this on other place
    private String trimAddressSuffix(String address) {
        String res = address.replaceAll(CONTEXT_PATH_SUFFIX, "");
        System.out.println(res);
        return res;
    }
}
