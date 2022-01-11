package de.ikor.sip.foundation.core.actuator.routes;

import lombok.Getter;
import lombok.Setter;

/** POJO object for storing the markdown file */
@Getter
public class MarkdownObject {

  private String name;
  @Setter private String content;

  public MarkdownObject(String name) {
    this.name = name;
  }
}
