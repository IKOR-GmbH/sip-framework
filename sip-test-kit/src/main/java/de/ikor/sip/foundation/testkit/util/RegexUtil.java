package de.ikor.sip.foundation.testkit.util;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

/** Util class for comparing a string to a pattern */
@Service
public class RegexUtil {
  private RegexUtil() {}

  /** List of characters that will be escaped in the pattern */
  private static final List<String> CHARS_TO_ESCAPE =
      Arrays.asList("\\{", "\\}", "\\[", "\\]", "\\$");

  /**
   * Compare a string to a pattern
   *
   * @param expected used to create a regex pattern
   * @param actual string that should be matched
   * @return true if string matches the pattern, otherwise false
   */
  public static boolean compare(String expected, String actual) {
    if (areBothBlank(actual, expected)) {
      return true;
    } else if (isOneBlank(actual, expected)) {
      return false;
    }
    String expectedPattern = reformatEscapeCharacter(expected);
    return Pattern.compile(expectedPattern).matcher(Objects.requireNonNull(actual)).find();
  }

  /**
   * Replaces escape characters
   *
   * @param str - String to be reformated
   * @return String - either null if param null or reformatted param string
   */
  public static String reformatEscapeCharacter(String str) {
    if (str == null) {
      return null;
    }

    String pattern = str;
    for (String escCharacter : CHARS_TO_ESCAPE) {
      pattern =
          pattern.replaceAll(
              escCharacter,
              escCharacter.equals("\\$") ? "\\\\" + escCharacter : "\\" + escCharacter);
    }
    return pattern;
  }

  private static boolean isOneBlank(String actual, String expected) {
    return isBlank(actual) || isBlank(expected);
  }

  private static boolean areBothBlank(String actual, String expected) {
    return isBlank(expected) && isBlank(actual);
  }
}
