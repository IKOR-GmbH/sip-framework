package de.ikor.sip.foundation.core.actuator.routes;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Optional;

/** POJO object for storing the markdown file */
@Getter
@Slf4j
public class MarkdownObject {

  private String name;
  @Setter private String content;

  public MarkdownObject(String name) {
    this.name = name;
  }

  public static Optional<MarkdownObject> createMdObject(File file) {
    MarkdownObject mdObject = new MarkdownObject(file.getName());

    try (BufferedReader reader =
                 new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
      mdObject.setContent(FileCopyUtils.copyToString(reader));
    } catch (Exception e) {
      log.warn("sip.core.actuator.info.filebadcontent_{}", mdObject.getName());
    }
    return Optional.of(mdObject);
  }
}
