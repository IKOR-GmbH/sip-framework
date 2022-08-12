package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.headers;

/** Enum with Camel File, FTP, FTPS, SFTP exchange headers which we can set programmatically */
public enum FileExchangeHeaders {
  CAMEL_FILE_NAME("CamelFileName"),
  CAMEL_FILE_LENGTH("CamelFileLength"),
  CAMEL_FILE_NAME_CONSUMED("CamelFileNameConsumed"),
  CAMEL_FILE_NAME_ONLY("CamelFileNameOnly"),
  CAMEL_FILE_LAST_MODIFIED("CamelFileLastModified"),
  CAMEL_MESSAGE_TIMESTAMP("CamelMessageTimestamp");

  FileExchangeHeaders(String headerKey) {
    this.headerKey = headerKey;
  }

  private final String headerKey;

  /**
   * Getter for header key
   *
   * @return String
   */
  public String getValue() {
    return this.headerKey;
  }
}
