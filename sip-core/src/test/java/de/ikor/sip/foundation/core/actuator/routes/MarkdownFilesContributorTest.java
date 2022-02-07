package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;

class MarkdownFilesContributorTest {

  private static final String BUILD_KEY = "build";
  private static final String FILES_KEY = "files";

  private MarkdownFilesContributor subject;
  private Info.Builder builder;

  @BeforeEach
  void setup() {
    subject = new MarkdownFilesContributor();
    builder = new Info.Builder();
  }

  @Test
  void Given_markdownFilesInDocumentsFolder_When_contribute_Then_returnOnlyMarkdownFiles() {
    // act
    subject.contribute(builder);

    // assert
    LinkedHashMap<String, Object> buildInfoResult =
        (LinkedHashMap<String, Object>) builder.build().get(BUILD_KEY);
    List<MarkdownObject> resultFiles = (List<MarkdownObject>) buildInfoResult.get(FILES_KEY);

    assertThat(buildInfoResult).hasSize(1);
    assertThat(resultFiles).hasSize(3);
  }
}
