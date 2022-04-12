package de.ikor.sip.foundation.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class CoreCamelSpringBootTest {

  @Autowired CamelContext camelContext;

  @Autowired private MockMvc mvcBean;

  @Test
  void WHEN_appIsStarted_VERIFY_onlyTestRouteExists() {
    assertThat(camelContext).isNotNull();
    assertThat(camelContext.getRoutes()).hasSize(1);
    assertThat(camelContext.getRoute(CoreTestApplication.TEST_ROUTE_ID)).isNotNull();
  }

  @Test
  void WHEN_appIsStarted_VERIFY_CamelHealthOK() throws Exception {
    mvcBean
        .perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }
}
