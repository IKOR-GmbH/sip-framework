package de.ikor.sip.testkit.configurationproperties;

import com.fasterxml.jackson.annotation.*;
import de.ikor.sip.testkit.configurationproperties.models.EndpointProperties;
import java.util.*;
import lombok.Data;

/** Definition of a single test case. */
@Data
public class TestCaseDefinition {
  @JsonProperty("schema_version")
  private double schemaVersion;

  private String title = UUID.randomUUID().toString();

  @JsonProperty("when-execute")
  private EndpointProperties whenExecute;

  @JsonProperty("with-mocks")
  private List<EndpointProperties> withMocks = new ArrayList<>();

  @JsonProperty("then-expect")
  private List<EndpointProperties> thenExpect = new ArrayList<>();
}
