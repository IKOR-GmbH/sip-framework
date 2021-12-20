package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;

class MarkdownFileContributorTest {

  @Test
  void mapSimpleObject() {
    Object o = new Object();
    Info info = contributeFrom();
    ArrayList<MarkdownObject> filesArray = (ArrayList<MarkdownObject>) info.get("files");
    MarkdownObject file1 = (filesArray).get(0);
    final AbstractStringAssert<?> files = assertThat(file1.getMdContent()).isNotEmpty();
  }

  private static Info contributeFrom() {
    MarkdownFileContributor contributor = new MarkdownFileContributor();
    Info.Builder builder = new Info.Builder();
    contributor.contribute(builder);
    return builder.build();
  }
}
