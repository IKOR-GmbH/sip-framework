package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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

class DetailsInfoContributorTest {

  private static final String BUILD_KEY = "build";
  private static final String FILES_KEY = "files";
  private static final String ADAPTER_NAME_DETAILS_KEY = "adapter-name";
  private static final String ADAPTER_NAME_BUILD_KEY = "name";
  private static final String ADAPTER_VERSION_DETAILS_KEY = "adapter-version";
  private static final String ADAPTER_VERSION_BUILD_KEY = "version";
  private static final String SIP_FRAMEWORK_VERSION_DETAILS_KEY = "sip-framework-version";
  private static final String SIP_FRAMEWORK_VERSION_BUILD_KEY = "sipFrameworkVersion";

  private static final String ADAPTER_NAME_TEST = "testName";
  private static final String ADAPTER_VERSION_TEST = "1.0.2";
  private static final String SIP_FRAMEWORK_VERSION_TEST = "1.0.1";

  private static final String FILENAME_MD = "readme.md";
  private static final String FILENAME_TXT = "test.txt";
  private static final String FILE_CONTENT = "Test data";

  private DetailsInfoContributor subject;
  private Info.Builder builder;

  @BeforeEach
  void setup() {
    subject = new DetailsInfoContributor();
    builder = new Info.Builder();
  }

  @Test
  void Given_buildInfo_When_contribute_Then_returnBasicAdapterInfo() {
    // arrange
    builder.withDetail(BUILD_KEY, createBuildInfo());

    // act
    subject.contribute(builder);

    // assert
    LinkedHashMap<String, Object> buildInfoResult =
        (LinkedHashMap<String, Object>) builder.build().get(BUILD_KEY);
    assertThat(buildInfoResult.size()).isEqualTo(4);
    assertThat(buildInfoResult.get(ADAPTER_NAME_DETAILS_KEY)).isEqualTo(ADAPTER_NAME_TEST);
    assertThat(buildInfoResult.get(ADAPTER_VERSION_DETAILS_KEY)).isEqualTo(ADAPTER_VERSION_TEST);
    assertThat(buildInfoResult.get(SIP_FRAMEWORK_VERSION_DETAILS_KEY))
        .isEqualTo(SIP_FRAMEWORK_VERSION_TEST);
  }

  @Test
  void Given_nullBuildInfo_When_contribute_Then_returnNoBasicAdapterInfo() {
    // arrange
    builder.withDetail(BUILD_KEY, null);

    // act
    subject.contribute(builder);

    // assert
    LinkedHashMap<String, Object> buildInfoResult =
        (LinkedHashMap<String, Object>) builder.build().get(BUILD_KEY);
    assertThat(buildInfoResult.size()).isEqualTo(1);
    assertThat(buildInfoResult.get(ADAPTER_NAME_DETAILS_KEY)).isNull();
    assertThat(buildInfoResult.get(ADAPTER_VERSION_DETAILS_KEY)).isNull();
    assertThat(buildInfoResult.get(SIP_FRAMEWORK_VERSION_DETAILS_KEY)).isNull();
  }

  @Test
  void Given_markdownAndTextFiles_When_contribute_Then_returnOnlyMarkdownFileContentAndName()
      throws IOException {
    // arrange
    File file1 = createFile(FILENAME_MD, FILE_CONTENT);
    File file2 = createFile(FILENAME_TXT, FILE_CONTENT);

    File[] testFiles = new File[2];
    testFiles[0] = file1;
    testFiles[1] = file2;

    File folder = mock(File.class);
    when(folder.listFiles()).thenReturn(testFiles);

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

    assertThat(buildInfoResult.size()).isEqualTo(1);
    assertThat(resultFiles.size()).isEqualTo(1);
    assertThat(mdFile.getName()).isEqualTo(FILENAME_MD);
    assertThat(mdFile.getContent()).isEqualTo(FILE_CONTENT);
  }

  @Test
  void Given_noMarkdownFiles_When_contribute_Then_returnMissingMdFilesLog() throws IOException {
    // arrange
    Logger logger =
        (Logger)
            LoggerFactory.getLogger(
                "de.ikor.sip.foundation.core.actuator.routes.DetailsInfoContributor");
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    List<ILoggingEvent> logsList = listAppender.list;

    // act
    subject.contribute(builder);

    // assert
    assertThat(logsList.get(0).getMessage()).isEqualTo("sip.core.actuator.info.missingmdfiles");
  }

  private LinkedHashMap<String, Object> createBuildInfo() {
    LinkedHashMap<String, Object> buildInfo = new LinkedHashMap<>();
    buildInfo.put(ADAPTER_NAME_BUILD_KEY, ADAPTER_NAME_TEST);
    buildInfo.put(ADAPTER_VERSION_BUILD_KEY, ADAPTER_VERSION_TEST);
    buildInfo.put(SIP_FRAMEWORK_VERSION_BUILD_KEY, SIP_FRAMEWORK_VERSION_TEST);
    return buildInfo;
  }

  private File createFile(String fileName, String fileContent) throws IOException {
    File file = new File(fileName);
    FileWriter writer = new FileWriter(file);
    writer.write(fileContent);
    writer.close();
    return file;
  }
}
