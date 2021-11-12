package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "sip.core.tracing.limit=5")
@ContextConfiguration(classes = {TraceHistory.class})
class TraceHistoryTest {

  @Autowired private TraceHistory traceHistory;

  @Test
  void WHEN_add_WITH_message_THEN_messageAdded() {
    // arrange
    String message = UUID.randomUUID().toString();

    // act
    traceHistory.add(message);

    // assert
    assertThat(traceHistory.getAndClearHistory()).containsExactly(message);
  }

  @Test
  void WHEN_add_WITH_limitReached_THEN_firstMessageRemoved() {
    // arrange

    // act
    for (int i = 0; i < 6; i++) {
      traceHistory.add("message" + i);
    }

    // assert
    assertThat(traceHistory.getAndClearHistory()).doesNotContain("message0");
  }

  @Test
  void WHEN_getAndClearHistory_THEN_historyEmptyAfterwards() {
    // arrange
    String message = UUID.randomUUID().toString();
    traceHistory.add(message);

    // act
    traceHistory.getAndClearHistory();

    // assert
    assertThat(traceHistory.getAndClearHistory()).isEmpty();
  }
}
