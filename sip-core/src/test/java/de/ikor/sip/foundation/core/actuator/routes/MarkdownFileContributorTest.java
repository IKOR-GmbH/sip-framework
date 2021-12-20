package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;

class MarkdownFileContributorTest {

  @Test
  void mapSimpleObject() {
    Object o = new Object();
    Info info = contributeFrom();
    assertThat((((ArrayList<MarkdownObject>) info.get("files")).get(0)).getMdContent())
        .isNotEmpty();
  }

  private static Info contributeFrom() {
    MarkdownFileContributor contributor = new MarkdownFileContributor();
    Info.Builder builder = new Info.Builder();
    contributor.contribute(builder);
    return builder.build();
  }
}
