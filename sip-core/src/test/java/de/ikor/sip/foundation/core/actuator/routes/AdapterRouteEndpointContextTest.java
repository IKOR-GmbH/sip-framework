package de.ikor.sip.foundation.core.actuator.routes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.ikor.sip.foundation.core.CoreTestApplication;
import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdapterRouteEndpointContextTest {

  private static final String NON_EXISTENT_ROUTE_ID = "falseRouteId";

  @Autowired private MockMvc mvcBean;

  @Autowired private CamelContext camelContext;

  @Test
  void When_callingAdapterRoutesEndpoint_Then_httpSuccessReceived() throws Exception {
    mvcBean.perform(get("/actuator/adapter-routes")).andExpect(status().is2xxSuccessful());
  }

  @Test
  void When_callingAdapterRouteEndpoint_With_ValidRoute_Then_httpSuccessReceived()
      throws Exception {
    mvcBean
        .perform(get("/actuator/adapter-routes/" + CoreTestApplication.TEST_ROUTE_ID))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void When_callingAdapterRouteEndpoint_With_InvalidRoute_Then_httpNotFoundReceived()
      throws Exception {
    mvcBean
        .perform(get("/actuator/adapter-routes/" + NON_EXISTENT_ROUTE_ID))
        .andExpect(status().isNotFound());
  }

  @Test
  void When_callingAdapterRouteResetEndpoint_With_InvalidRoute_Then_httpNotFoundReceived()
      throws Exception {
    mvcBean
        .perform(post("/actuator/adapter-routes/" + NON_EXISTENT_ROUTE_ID + "/reset"))
        .andExpect(status().isNotFound());
  }
}
