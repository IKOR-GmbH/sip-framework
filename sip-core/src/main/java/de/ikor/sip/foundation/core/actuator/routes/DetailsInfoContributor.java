package de.ikor.sip.foundation.core.actuator.routes;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

/**
 * {@link DetailsInfoContributor} extends {@link InfoContributor} to add needed information to the
 * info actuator
 */
@Slf4j
@Component
public class DetailsInfoContributor implements InfoContributor {

  private static final String MARKDOWN_EXTENSION = ".md";
  private static final String BUILD_KEY = "build";
  private static final String FILES_KEY = "files";
  private static final String ADAPTER_NAME_DETAILS_KEY = "adapter-name";
  private static final String ADAPTER_NAME_BUILD_KEY = "name";
  private static final String ADAPTER_VERSION_DETAILS_KEY = "adapter-version";
  private static final String ADAPTER_VERSION_BUILD_KEY = "version";
  private static final String SIP_FRAMEWORK_VERSION_DETAILS_KEY = "sip-framework-version";
  private static final String SIP_FRAMEWORK_VERSION_BUILD_KEY = "sipFrameworkVersion";

  /**
   * Adding Markdown files {@link MarkdownObject} and base project information in actuator/info page.
   *
   * @param builder Info.Builder of the SpringBoot actuator specification
   */
  @SneakyThrows
  @Override
  public void contribute(Info.Builder builder) {
    Map<String, Object> buildInfo = (LinkedHashMap<String, Object>) builder.build().get(BUILD_KEY);

    if (buildInfo != null) {
      collectAdapterInfo(buildInfo);
    } else {
      buildInfo = new LinkedHashMap<>();
      builder.withDetail(BUILD_KEY, buildInfo);
    }

    buildInfo.put(FILES_KEY, fetchMarkdownObjects());
  }

  private void collectAdapterInfo(Map<String, Object> buildInfo) {
    String adapterName = (String) buildInfo.get(ADAPTER_NAME_BUILD_KEY);
    String adapterVersion = (String) buildInfo.get(ADAPTER_VERSION_BUILD_KEY);
    String sipFrameworkVersion = (String) buildInfo.get(SIP_FRAMEWORK_VERSION_BUILD_KEY);

    clearOriginalBuildInfo(buildInfo);

    buildInfo.put(ADAPTER_NAME_DETAILS_KEY, adapterName);
    buildInfo.put(ADAPTER_VERSION_DETAILS_KEY, adapterVersion);
    buildInfo.put(SIP_FRAMEWORK_VERSION_DETAILS_KEY, sipFrameworkVersion);
  }

  private void clearOriginalBuildInfo(Map<String, Object> buildInfo) {
    buildInfo.clear();
  }

  private List<MarkdownObject> fetchMarkdownObjects() {
    List<MarkdownObject> result = new ArrayList<>();
    File folder = new File("./");
    List<File> files =
        Arrays.stream(folder.listFiles())
            .filter((file -> file.getName().toLowerCase().endsWith(MARKDOWN_EXTENSION)))
            .collect(Collectors.toList());

    if (!files.isEmpty()) {
      result = createMdObjectsList(files);
    } else {
      log.info("sip.core.actuator.info.missingmdfiles");
    }
    return result;
  }

  private List<MarkdownObject> createMdObjectsList(List<File> files) {
    List<MarkdownObject> mdFiles = new ArrayList<>();
    for (File file : files) {
      Optional<MarkdownObject> mdObject = createMdObject(file);
      mdFiles.add(mdObject.get());
    }
    return mdFiles;
  }

  private Optional<MarkdownObject> createMdObject(File file) {
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
