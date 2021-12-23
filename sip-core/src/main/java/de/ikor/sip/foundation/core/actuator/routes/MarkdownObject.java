package de.ikor.sip.foundation.core.actuator.routes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class MarkdownObject {

  private static final String CONTENT = "content";
  private String fileName;
  private String fileDescription;

  @Setter private String mdContent;
}
