package de.ikor.sip.foundation.core.util;

import static de.ikor.sip.foundation.core.util.StreamHelper.typeFilter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class StreamHelperTest {

  @Test
  void WHEN_listHasStringElements_THEN_onlyStringElementsRemain() {
    // arrange
    List<Object> elements = List.of("elem1", "elem2", 123);

    // act
    List<String> elementsFiltered = elements.stream().flatMap(typeFilter(String.class)).toList();

    // assert
    assertThat(elementsFiltered).hasSize(2).contains("elem1", "elem2");
  }

  @Test
  void WHEN_listHasNoStringElements_THEN_emptyStreamRemains() {
    // arrange
    List<Object> elements = List.of(321, Collections.emptyList());

    // act
    List<String> elementsFiltered = elements.stream().flatMap(typeFilter(String.class)).toList();

    // assert
    assertThat(elementsFiltered).isEmpty();
  }
}
