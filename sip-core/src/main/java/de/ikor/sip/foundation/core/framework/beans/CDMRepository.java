package de.ikor.sip.foundation.core.framework.beans;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CDMRepository {
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
