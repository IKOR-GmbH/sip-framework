package de.ikor.sip.foundation.core;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.camel.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CoreCamelSpringBootContextTest {

  @Autowired CamelContext camelContext;

  @Autowired private MockMvc mvcBean;

  @Test
  void WHEN_appIsStarted_VERIFY_routeExists() throws Exception {
    assertThat(camelContext).isNotNull();
    assertThat(camelContext.getRoutes()).hasSize(1);
    assertThat(camelContext.getRoute(CoreTestApplication.TEST_ROUTE_ID)).isNotNull();
    mvcBean
        .perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }
}
