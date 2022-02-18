package de.ikor.sip.foundation.core.actuator.routes;

import java.io.*;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * {@link MarkdownFilesContributor} extends {@link InfoContributor} to add markdown files to the
 * info actuator
 */
@Slf4j
@Component
public class MarkdownFilesContributor implements InfoContributor {

  private static final String BUILD_KEY = "build";
  private static final String FILES_KEY = "files";

  /**
   * Adding Markdown files {@link MarkdownObject} in actuator/info page.
   *
   * @param builder Info.Builder of the SpringBoot actuator specification.
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

  private List<MarkdownObject> fetchMarkdownObjects() throws IOException {
    List<MarkdownObject> mdFiles = new ArrayList<>();

    for (Resource resource : fetchFilesFromDirectory()) {
      Optional<MarkdownObject> mdObject = MarkdownObject.createMdObject(resource);
      if (mdObject.isPresent()) {
        mdFiles.add(mdObject.get());
      }
    }

    if (mdFiles.isEmpty()) {
      log.warn("sip.core.actuator.info.missingmdfiles");
    }
    return mdFiles;
  }

  private Resource[] fetchFilesFromDirectory() throws IOException {
    ClassLoader classloader = this.getClass().getClassLoader();
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classloader);
    return resolver.getResources("classpath*:/documents/*.md");
  }
}
