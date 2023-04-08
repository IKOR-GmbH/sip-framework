package de.ikor.sip.foundation.soap.declarative.connector;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import org.junit.jupiter.api.Test;

class SoapOperationOutboundConnectorBaseTest {

  @OutboundConnector(
      connectorId = "testConnector",
      integrationScenario = "testScenario",
      connectorGroup = "testGroup",
      requestModel = String.class)
  private class SOAPOutboundConnector extends SoapOperationOutboundConnectorBase {

    @Override
    public String getServiceOperationName() {
      return "testOperation";
    }
  }

  @Test
  void WHEN_OutboundConnectorNoServiceClassCanBeInferred_THEN_SIPFrameworkExceptionIsThrown() {
    // arrange
    SoapOperationOutboundConnectorBase outboundConnector = new SOAPOutboundConnector();

    // act & assert
    // act & assert
    assertThatThrownBy(
            () -> {
              outboundConnector.getServiceInterfaceClass();
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            String.format(
                "SIP Framework can't infer Service class of %s Outbound SOAP Connector. Please @Override getServiceInterfaceClass() method.",
                SOAPOutboundConnector.class.getName()));
  }
}
