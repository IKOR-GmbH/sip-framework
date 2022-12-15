package de.ikor.sip.foundation.core.framework.beans;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CDMRepository {
  // TODO CDM is not perfect naming as it handles more than CDM objects
  private final Map<String, List<Object>> cdms = new HashMap<>();

  public void put(String key, Object value) {
    List current = cdms.getOrDefault(key, new LinkedList<>());
    if (!current.contains(value)) {
      current.add(value);
    }
    cdms.putIfAbsent(key, current);
  }

  public Object getFirst(String key) {
    List current = cdms.get(key);
    return current.get(0);
  }

  public Object getLast(String key) {
    List current = cdms.get(key);
    return current.get(current.size() - 1);
  }

  public Object getAt(String key, int i) {
    List current = cdms.get(key);
    return current.get(i);
  }
}
