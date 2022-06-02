package de.ikor.sip.foundation.testkit.util;

import lombok.RequiredArgsConstructor;
import org.apache.camel.*;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.soap.*;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/** Util class that executes a request to a certain route (defined in the Exchange) */
@Component
@RequiredArgsConstructor
public class SIPRouteProducerTemplate {

  // WIP - Consider implementing this on other place
  private static final String CONTEXT_PATH_SUFFIX = "[/]$";
  private static final String CONTEXT_PATH_PREFIX = "[/]";

  private final ProducerTemplate producerTemplate;
  private final SIPEndpointResolver sipEndpointResolver;

  // WIP - Consider implementing this on other place
  @Value("${sip.adapter.camel-cxf-endpoint-context-path}")
  private String cxfContextPath = "";

  /**
   * Request an exchange on camel route
   *
   * @param exchange {@link Exchange} that is sent to a route
   * @return {@link Exchange} result of request
   */
  public Exchange requestOnRoute(Exchange exchange) {
    // OLD CODE
//    String endpointURI = sipEndpointResolver.resolveURI(exchange);
//    return producerTemplate.send(endpointURI, exchange);

    // WIP - Experimental code for testing SOAP
    InputStream is = new ByteArrayInputStream(exchange.getIn().getBody(String.class).getBytes());
    try {
      MessageFactory mf = MessageFactory.newInstance();
      MimeHeaders header = new MimeHeaders();
      header.addHeader("Content-Type", "text/xml");
      SOAPMessage msg = mf.createMessage(header, is);
      SOAPBody soapBody = msg.getSOAPBody();

      DOMSource source = new DOMSource(soapBody.getFirstChild());
      StringWriter stringResult = new StringWriter();
      TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
      String message = stringResult.toString();
      exchange.getIn().setBody(message);

      String operationName = soapBody.getFirstChild().getLocalName();
      System.out.println("Print service name: " + operationName);
      exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, operationName);

    } catch (Exception e) {
      e.printStackTrace();
    }

    CxfEndpoint cxfEndpoint = (CxfEndpoint) sipEndpointResolver.resolveCxfEndpoint(exchange);
    String address = trimAddressPrefix(cxfEndpoint.getAddress());
    cxfEndpoint.setAddress("http://localhost:8081" + trimAddressSuffix(cxfContextPath) + "/" + address);

    return producerTemplate.send(cxfEndpoint, exchange);

    // Works for RAW data format
//    producerTemplate.sendBody(cxfEndpoint, exchange.getIn().getBody(String.class));

//    producerTemplate.sendBody("http://localhost:8081/quoteSubmission", exchange.getIn().getBody(String.class));
//    return producerTemplate.requestBody(cxfEndpoint, exchange.getIn().getBody(String.class), Exchange.class);
//    producerTemplate.requestBody(cxfEndpoint, exchange.getIn().getBody(String.class));

//    producerTemplate.sendBodyAndHeader(cxfEndpoint, exchange.getIn().getBody(), "operationName", exchange.getIn().getHeader("operationName"));

//    return null;
  }

  // WIP - Consider implementing this on other place
  private String trimAddressSuffix(String address) {
    String res = address.replaceAll(CONTEXT_PATH_SUFFIX, "");
    System.out.println(res);
    return res;
  }

  // WIP - Consider implementing this on other place
  private String trimAddressPrefix(String address) {
    String res = address.replaceFirst(CONTEXT_PATH_PREFIX, "");
    System.out.println(res);
    return res;
  }
}
