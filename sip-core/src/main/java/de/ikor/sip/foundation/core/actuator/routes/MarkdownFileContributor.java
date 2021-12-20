package de.ikor.sip.foundation.core.actuator.routes;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * {@link MarkdownFileContributor} extends {@link InfoContributor} to add needed information to the
 * info actuator
 */
@Component
public class MarkdownFileContributor implements InfoContributor {

  private static final Logger logger = LoggerFactory.getLogger(MarkdownFileContributor.class);

  /**
   * Entry point. {@link MarkdownObject} List of MarkdownObjects with its informtion is crated and
   * added to the actuator Info.Builder
   *
   * @param builder Info.Builder of the SpringBoot actuator specification
   */
  @SneakyThrows
  @Override
  public void contribute(Info.Builder builder) {

    ArrayList<MarkdownObject> files = new ArrayList<>();

    MarkdownObject readMe = new MarkdownObject("README.md", "Read me", "");
    MarkdownObject changeLog = new MarkdownObject("changelog.md", "Change log", "");

    files.add(readMe);
    files.add(changeLog);

    ArrayList<MarkdownObject> mdFiles = new ArrayList<> ();

    for (MarkdownObject obj : files) {

      addMdContentValue(obj);
      mdFiles.add(obj);
    }

    builder.withDetail("files", mdFiles);
  }

  /**
   * Logic fills the object with mdContent if the file exists on the Classpath. Otherwise it is
   * null.
   *
   * @param mdObj - {@link MarkdownObject} is filled with mdContent if the file exists on the Classpath
   */
  private void addMdContentValue(MarkdownObject mdObj) {

    InputStream is = getClass().getClassLoader().getResourceAsStream(mdObj.getFileName());

    String data;
    try {
      data = readFromInputStream(is);
      mdObj.setMdContent(data);
    } catch (IOException e) {

      logger.error(e.toString());
    } finally {
      try {
        if (is != null) is.close();
      } catch (Exception e) {
        // nothing
      }
    }
  }

  /**
   * Read from InputStream and return a text representation of file contents.
   *
   * @param inputStream - InputStream of the MD file
   * @return String - File contents
   * @throws IOException - in case file cannot be read
   */
  private String readFromInputStream(InputStream inputStream) throws IOException {
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
      }
    }
    return resultStringBuilder.toString();
  }
}
