package de.ikor.sip.foundation.core.actuator.health.camel;

import static de.ikor.sip.foundation.core.CoreTestApplication.TEST_ROUTE_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CamelHealthCheckRepositoryOverrideTest {

  @Autowired private MockMvc mvcBean;

  @Autowired private CamelContext camelContext;

  @Test
  void When_callingHealthCheckEndpoint_Verify_httpSuccessReceived() throws Exception {
    mvcBean.perform(get("/actuator/health")).andExpect(status().is2xxSuccessful());
  }

  @Test
  void When_suspendingRoute_Verify_statusDown() throws Exception {
    System.out.println(camelContext.getRoutes());
    verifyTestRouteIsState("UP");
    mvcBean
        .perform(post("/actuator/adapter-routes/" + TEST_ROUTE_ID + "/suspend"))
        .andExpect(status().is2xxSuccessful());
    verifyTestRouteIsState("DOWN");
    mvcBean.perform(post("/actuator/adapter-routes/" + TEST_ROUTE_ID + "/start"));
    verifyTestRouteIsState("UP");
  }

  private void verifyTestRouteIsState(String state) throws Exception {
    mvcBean
        .perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.components.camelHealth.details.consumer:" + TEST_ROUTE_ID).value(state))
        .andExpect(
            jsonPath("$.components.camelHealth.details.route:" + TEST_ROUTE_ID).value(state));
  }
}
