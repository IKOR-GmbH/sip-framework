package de.ikor.sip.foundation.testkit.util;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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
    boolean result = false;
    if (areExpectedAndActualEmpty(actual, expected)) {
      result = true;
    } else if (shouldDoComparison(actual, expected)) {
      String expectedPattern = reformatEscapeCharacter(expected);
      result = Pattern.compile(expectedPattern).matcher(Objects.requireNonNull(actual)).find();
    }
    return result;
  }

  /**
   * Replaces escape characters
   *
   * @param str - String to be reformated
   * @return String - either null if param null or reformated param string
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

  private static boolean shouldDoComparison(String actual, String expected) {
    // avoid NPE in regex compare and matching any empty string
    return actual != null && isNotEmpty(expected);
  }

  private static boolean areExpectedAndActualEmpty(String actual, String expected) {
    // match if actual body is empty or null when expected is defined as empty
    return isEmpty(expected) && isEmpty(actual);
  }
}
