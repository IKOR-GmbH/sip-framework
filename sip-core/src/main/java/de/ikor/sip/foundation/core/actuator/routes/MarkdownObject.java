package de.ikor.sip.foundation.core.actuator.routes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class MarkdownObject {

  private String fileName;
  private String fileDescription;
  private static final String CONTENT = "content";

  @Setter private String mdContent;
}
