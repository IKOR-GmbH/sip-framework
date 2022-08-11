package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.headers;

/**
 * Enum with Camel FTP, FTPS, SFTP exchange headers which we can set programmatically
 */
public enum FtpExchangeHeaders {

    CAMEL_FILE_NAME("CamelFileName"),
    CAMEL_FILE_ABSOLUTE("CamelFileAbsolute"),
    CAMEL_FILE_HOST("CamelFileHost"),
    CAMEL_FILE_LENGTH("CamelFileLength"),
    CAMEL_FILE_PARENT("CamelFileParent"),
    CAMEL_FILE_NAME_CONSUMED("CamelFileNameConsumed"),
    CAMEL_FILE_NAME_ONLY("CamelFileNameOnly"),
    CAMEL_FILE_RELATIVE_PATH("CamelFileRelativePath"),
    CAMEL_FILE_ABSOLUTE_PATH("CamelFileAbsolutePath"),
    CAMEL_FILE_PATH("CamelFilePath"),
    CAMEL_FILE_LAST_MODIFIED("CamelFileLastModified"),
    CAMEL_MESSAGE_TIMESTAMP("CamelMessageTimestamp"),
    CAMEL_REMOTE_FILE_INPUT_STREAM("CamelRemoteFileInputStream");

    FtpExchangeHeaders(String headerKey) {
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
