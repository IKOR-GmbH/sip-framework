package de.ikor.sip.foundation.core.util;

import java.util.function.Function;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StreamHelper {

  /**
   * Filters a stream of objects by type.
   *
   * <p>Usage example: <code>List&lt;Object&gt;.stream().flatMap(typeFilter(String.class)).toList()
   * </code>
   *
   * @param type The filtering type
   * @return Filtering function
   * @param <S> The type of the stream
   * @param <T> Filtering type
   */
  public static <S, T extends S> Function<S, Stream<T>> typeFilter(final Class<T> type) {
    return entry -> type.isInstance(entry) ? Stream.of(type.cast(entry)) : Stream.empty();
  }
}
