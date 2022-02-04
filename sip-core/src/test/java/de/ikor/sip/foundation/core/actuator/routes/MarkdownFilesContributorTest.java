package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.info.Info;

class MarkdownFilesContributorTest {

  private static final String BUILD_KEY = "build";
  private static final String FILES_KEY = "files";

  private static final String FILENAME_MD = "readme.md";
  private static final String FILENAME_TXT = "test.txt";
  private static final String FILE_CONTENT = "Test data";

  private MarkdownFilesContributor subject;
  private Info.Builder builder;

  @BeforeEach
  void setup() {
    subject = new MarkdownFilesContributor();
    builder = new Info.Builder();
  }

  @Test
  void Given_markdownAndTextFiles_When_contribute_Then_returnOnlyMarkdownFileContentAndName()
      throws IOException {
    // arrange
    File file1 = createFile(FILENAME_MD, FILE_CONTENT);
    File file2 = createFile(FILENAME_TXT, FILE_CONTENT);

    // act
    subject.contribute(builder);

    // Leave environment clean
    file1.delete();
    file2.delete();

    // assert
    LinkedHashMap<String, Object> buildInfoResult =
        (LinkedHashMap<String, Object>) builder.build().get(BUILD_KEY);
    List<MarkdownObject> resultFiles = (List<MarkdownObject>) buildInfoResult.get(FILES_KEY);
    MarkdownObject mdFile = resultFiles.get(0);

    assertThat(buildInfoResult).hasSize(1);
    assertThat(resultFiles).hasSize(1);
    assertThat(mdFile.getName()).isEqualTo(FILENAME_MD);
    assertThat(mdFile.getContent()).isEqualTo(FILE_CONTENT);
  }

  @Test
  void Given_noMarkdownFiles_When_contribute_Then_returnMissingMdFilesLog() throws IOException {
    // arrange
    Logger logger =
        (Logger)
            LoggerFactory.getLogger(
                "de.ikor.sip.foundation.core.actuator.routes.MarkdownFilesContributor");
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.contribute(builder);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo("sip.core.actuator.info.missingmdfiles");
  }

  private File createFile(String fileName, String fileContent) throws IOException {
    File file = new File(fileName);
    FileWriter writer = new FileWriter(file);
    writer.write(fileContent);
    writer.close();
    return file;
  }
}
