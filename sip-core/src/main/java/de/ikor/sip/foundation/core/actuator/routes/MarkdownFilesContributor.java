package de.ikor.sip.foundation.core.actuator.routes;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

/**
 * {@link MarkdownFilesContributor} extends {@link InfoContributor} to add needed information to the
 * info actuator
 */
@Slf4j
@Component
public class MarkdownFilesContributor implements InfoContributor {

  private static final String MARKDOWN_EXTENSION = ".md";
  private static final String BUILD_KEY = "build";
  private static final String FILES_KEY = "files";

  /**
   * Adding Markdown files {@link MarkdownObject} and base project information in actuator/info
   * page.
   *
   * @param builder Info.Builder of the SpringBoot actuator specification
   */
  @SneakyThrows
  @Override
  public void contribute(Info.Builder builder) {
    Map<String, Object> buildInfo = (LinkedHashMap<String, Object>) builder.build().get(BUILD_KEY);

    if (buildInfo == null) {
      buildInfo = new LinkedHashMap<>();
      builder.withDetail(BUILD_KEY, buildInfo);
    }
    buildInfo.put(FILES_KEY, fetchMarkdownObjects());
  }

  private List<MarkdownObject> fetchMarkdownObjects() {
    List<MarkdownObject> result = new ArrayList<>();
    File folder = fetchFilesFromDirectory("./");
    List<File> files =
        Arrays.stream(folder.listFiles())
            .filter((file -> file.getName().toLowerCase().endsWith(MARKDOWN_EXTENSION)))
            .collect(Collectors.toList());

    if (!files.isEmpty()) {
      result = createMdObjectsList(files);
    } else {
      log.warn("sip.core.actuator.info.missingmdfiles");
    }
    return result;
  }

  private File fetchFilesFromDirectory(String path) {
    return new File(path);
  }

  private List<MarkdownObject> createMdObjectsList(List<File> files) {
    List<MarkdownObject> mdFiles = new ArrayList<>();
    for (File file : files) {
      Optional<MarkdownObject> mdObject = MarkdownObject.createMdObject(file);
      if (mdObject.isPresent()) {
        mdFiles.add(mdObject.get());
      }
    }
    return mdFiles;
  }
}
