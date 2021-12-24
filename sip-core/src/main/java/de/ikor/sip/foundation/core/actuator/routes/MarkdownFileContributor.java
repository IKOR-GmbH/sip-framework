package de.ikor.sip.foundation.core.actuator.routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

/**
 * {@link MarkdownFileContributor} extends {@link InfoContributor} to add needed information to the
 * info actuator
 */
@Slf4j
@Component
public class MarkdownFileContributor implements InfoContributor {

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

    MarkdownObject readMe = new MarkdownObject("/README.md", "Read me", null);
    MarkdownObject changeLog = new MarkdownObject("/changelog.md", "Change log", null);

    files.add(readMe);
    files.add(changeLog);

    ArrayList<MarkdownObject> mdFiles = new ArrayList<>();

    for (MarkdownObject obj : files) {

      addMdContentValue(obj);

      if (obj.getMdContent() != null) {
        mdFiles.add(obj);
      }
    }

    if (!mdFiles.isEmpty()) {
      builder.withDetail("files", mdFiles);
    }
  }

  /**
   * Logic fills the object with mdContent if the file exists on the Classpath. Otherwise it is
   * null.
   *
   * @param mdObj - {@link MarkdownObject} is filled with mdContent if the file exists on the
   *     Classpath
   */
  private void addMdContentValue(MarkdownObject mdObj) {

    try (InputStream is = getClass().getResourceAsStream(mdObj.getFileName()) ) {
      addFromBuffReader(mdObj, is);
    } catch (IOException e) {
      log.warn(e.toString());
    }
  }

  private void addFromBuffReader(MarkdownObject mdObj, InputStream is) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      mdObj.setMdContent(FileCopyUtils.copyToString(reader));
    } catch (Exception e) {
      log.warn(e.toString());
    }
  }
}
