package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;

class MarkdownFilesContributorTest {

  private static final String BUILD_KEY = "build";
  private static final String FILES_KEY = "files";

  private static final String FILES_TARGET_TESTING_DIRECTORY = "target/test-classes/documents/";
  private static final String README_MD = "readme.md";
  private static final String CHANGELOG_MD = "changelog.md";
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
  void Given_markdownAndTextFilesInDocumentsFolder_When_contribute_Then_returnOnlyMarkdownFiles()
      throws Exception {
    // arrange
    Path directory = Paths.get(FILES_TARGET_TESTING_DIRECTORY);
    Files.createDirectories(directory);

    File file1 = createFile(FILES_TARGET_TESTING_DIRECTORY, README_MD, FILE_CONTENT);
    File file2 = createFile(FILES_TARGET_TESTING_DIRECTORY, CHANGELOG_MD, FILE_CONTENT);
    File file3 = createFile(FILES_TARGET_TESTING_DIRECTORY, FILENAME_TXT, FILE_CONTENT);

    // act
    subject.contribute(builder);

    // Keep environment clean
    file1.delete();
    file2.delete();
    file3.delete();
    Files.deleteIfExists(directory);

    // assert
    LinkedHashMap<String, Object> buildInfoResult =
        (LinkedHashMap<String, Object>) builder.build().get(BUILD_KEY);
    List<MarkdownObject> resultFiles = (List<MarkdownObject>) buildInfoResult.get(FILES_KEY);

    assertThat(buildInfoResult).hasSize(1);
    assertThat(resultFiles).hasSize(2);
  }

  private File createFile(String directory, String fileName, String fileContent)
      throws IOException {
    File file = new File(directory + fileName);
    FileWriter writer = new FileWriter(file);
    writer.write(fileContent);
    writer.close();
    return file;
  }
}
