package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil.TESTING_SUFFIX;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;

public class TestRouteBinder extends RouteBinder {

  public TestRouteBinder(String useCase, Class<?> centralModelRequest) {
    super(useCase, centralModelRequest, TESTING_SUFFIX);
  }

  @Override
  public void appendOutConnectorsSeq(OutConnector[] outConnectors) {
    CentralEndpointsRegister.putInTestingState();
    super.appendOutConnectorsSeq(outConnectors);
    CentralEndpointsRegister.putInActualState();
  }

  @Override
  public void appendOutConnectorsParallel(OutConnector[] outConnectors) {
    CentralEndpointsRegister.putInTestingState();
    super.appendOutConnectorsSeq(outConnectors);
    CentralEndpointsRegister.putInActualState();
  }
}
