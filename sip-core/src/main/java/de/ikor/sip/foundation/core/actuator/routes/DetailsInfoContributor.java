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
  private static final String DETAILS_KEY = "details";
  private static final String BUILD_KEY = "build";
  private static final String FILES_KEY = "files";
  private static final String ADAPTER_NAME_DETAILS_KEY = "adapter-name";
  private static final String ADAPTER_NAME_BUILD_KEY = "name";
  private static final String ADAPTER_VERSION_DETAILS_KEY = "adapter-version";
  private static final String ADAPTER_VERSION_BUILD_KEY = "version";
  private static final String SIP_FRAMEWORK_VERSION_DETAILS_KEY = "sip-framework-version";
  private static final String SIP_FRAMEWORK_VERSION_BUILD_KEY = "sipFrameworkVersion";

  /**
   * Adding the information in actuator/info page. Adding Markdown files {@link MarkdownObject}.
   * Adding base project information.
   *
   * @param builder Info.Builder of the SpringBoot actuator specification
   */
  @SneakyThrows
  @Override
  public void contribute(Info.Builder builder) {

    Map<String, Object> detailsInfo = new LinkedHashMap<>();
    Map<String, String> buildInfo = (LinkedHashMap<String, String>) builder.build().get(BUILD_KEY);

    List<MarkdownObject> mdFiles = fetchMarkdownObjects();

    if (buildInfo != null) {
      collectAdapterInfo(detailsInfo, buildInfo);
    }

    if (!mdFiles.isEmpty()) {
      detailsInfo.put(FILES_KEY, mdFiles);
    }

    if (!detailsInfo.isEmpty()) {
      builder.withDetail(DETAILS_KEY, detailsInfo);
    }
  }

  private void collectAdapterInfo(Map<String, Object> detailsInfo, Map<String, String> buildInfo) {
    String adapterName = buildInfo.get(ADAPTER_NAME_BUILD_KEY);
    String adapterVersion = buildInfo.get(ADAPTER_VERSION_BUILD_KEY);
    String sipFrameworkVersion = buildInfo.get(SIP_FRAMEWORK_VERSION_BUILD_KEY);

    detailsInfo.put(ADAPTER_NAME_DETAILS_KEY, adapterName);
    detailsInfo.put(ADAPTER_VERSION_DETAILS_KEY, adapterVersion);
    detailsInfo.put(SIP_FRAMEWORK_VERSION_DETAILS_KEY, sipFrameworkVersion);
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
      log.warn("sip.core.actuator.info.filebadcontent");
    }
    return Optional.of(mdObject);
  }
}
