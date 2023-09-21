package de.ikor.sip.foundation.testkit.workflow.thenphase.validator.impl.comparators;

import de.ikor.sip.foundation.testkit.util.RegexUtil;

public class RegexComparator implements StringComparator {
  @Override
  public ComparatorResult compare(String expected, String actual) {
    boolean matches = RegexUtil.compare(expected, actual);
    return new ComparatorResult(matches, null);
  }
}
