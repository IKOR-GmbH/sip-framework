package de.ikor.sip.foundation.testkit.util;

import lombok.RequiredArgsConstructor;
import org.apache.camel.*;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.SoapVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

  @Autowired
  private SoapWebServiceClient soapWebServiceClient;

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

    CxfEndpoint cxfEndpoint = (CxfEndpoint) sipEndpointResolver.resolveCxfEndpoint(exchange);
    return soapWebServiceClient.postSOAPXML(exchange, cxfEndpoint);

/**

    // WIP - Experimental code for testing SOAP
    InputStream is = new ByteArrayInputStream(exchange.getIn().getBody(String.class).getBytes());
    try {

      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      dbFactory.setNamespaceAware(true);
      DocumentBuilder builder = dbFactory.newDocumentBuilder();
      Document document = builder.parse(is);
      DOMSource domSource = new DOMSource(document);

      MessageFactory mf = MessageFactory.newInstance(javax.xml.soap.SOAPConstants.SOAP_1_1_PROTOCOL);
      SOAPMessage message1 = mf.createMessage();
      SOAPPart part = message1.getSOAPPart();

      part.setContent(domSource);
      message1.saveChanges();

      SOAPBody body = message1.getSOAPPart().getEnvelope().getBody();

//      MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
//      MimeHeaders header = new MimeHeaders();
//      header.addHeader("Content-Type", "text/xml");
//      SOAPMessage msg = mf.createMessage(header, is);
//      SOAPBody soapBody = msg.getSOAPBody();

//      SOAPEnvelope soapEnvelope = msg.getSOAPPart().getEnvelope();
//      SOAPHeader soapHeaders = msg.getSOAPHeader();

//      String ns1 = soapBody.getFirstChild().getNamespaceURI();


//      Message m = new SoapMessage();

//      SoapVersion version = Soap11.getInstance();
//      SoapMessage msg1 = new SoapMessage(version);
//      msg1.setContent(String.class, exchange.getIn().getBody(String.class));
//      msg1.ge

      String operationName = body.getFirstChild().getLocalName();
      System.out.println("Print service name: " + operationName);
      exchange.getIn().setHeader(CxfConstants.OPERATION_NAME, operationName);



      DOMSource source = new DOMSource(body.getFirstChild());
      StringWriter stringResult = new StringWriter();
      TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
      String message = stringResult.toString();

      exchange.getIn().setBody(message);
    } catch (Exception e) {
      e.printStackTrace();
    }


    CxfEndpoint cxfEndpoint = (CxfEndpoint) sipEndpointResolver.resolveCxfEndpoint(exchange);
    String address = trimAddressPrefix(cxfEndpoint.getAddress());
    cxfEndpoint.setAddress("http://localhost:8081" + trimAddressSuffix(cxfContextPath) + "/" + address);
    // IZVUCI PORT

    return producerTemplate.send(cxfEndpoint, exchange);

    // Works for RAW data format
//    producerTemplate.sendBody(cxfEndpoint, exchange.getIn().getBody(String.class));

//    producerTemplate.sendBody("http://localhost:8081/quoteSubmission", exchange.getIn().getBody(String.class));
//    return producerTemplate.requestBody(cxfEndpoint, exchange.getIn().getBody(String.class), Exchange.class);
//    producerTemplate.requestBody(cxfEndpoint, exchange.getIn().getBody(String.class));

//    producerTemplate.sendBodyAndHeader(cxfEndpoint, exchange.getIn().getBody(), "operationName", exchange.getIn().getHeader("operationName"));

//    return null;

 */
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
