package de.ikor.sip.foundation.core.actuator.routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.spi.annotations.Component;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

@Component("MarkdownFileContributor")
public class MarkdownFileContributor implements InfoContributor {

  @Override
  public void contribute(Info.Builder builder) {
    Map<String, String> mdDetails = new HashMap<>();
    InputStream is = getClass().getClassLoader().getResourceAsStream("readme.md");

    String data = null;
    try {
      data = readFromInputStream(is);
    } catch (IOException e) {

      e.printStackTrace();
    } finally {
      try {
        assert is != null;
        is.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    mdDetails.put("readme.md", data);

    builder.withDetail("markdownFiles", mdDetails);
  }

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
