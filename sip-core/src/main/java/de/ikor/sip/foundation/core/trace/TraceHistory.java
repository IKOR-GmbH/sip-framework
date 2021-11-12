package de.ikor.sip.foundation.core.trace;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Contains history as list of traced exchanges */
@Component
@AllArgsConstructor
public class TraceHistory {

  /** List of trace history entries */
  private final LinkedList<String> list = new LinkedList<>();

  @Value("${sip.core.tracing.limit:100}")
  private final int limit;

  /**
   * Add tracing message to trace history list
   *
   * @param message information about traced exchange
   */
  @Synchronized
  public void add(String message) {
    if (list.size() >= limit) {
      list.removeFirst();
    }
    list.addLast(message);
  }

  /**
   * Retrieves and removes all elements from the history list
   *
   * @return the current tracing history
   */
  public List<String> getAndClearHistory() {
    List<String> retVal = new ArrayList<>(list);
    list.clear();
    return retVal;
  }
}
