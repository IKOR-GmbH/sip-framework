package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.trace.model.TraceUnit;
import java.util.List;
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

  @Autowired private TraceHistory subject;

  @Test
  void WHEN_add_WITH_message_THEN_messageAdded() {
    // arrange
    TraceUnit traceUnit = new TraceUnit();

    // act
    subject.add(traceUnit);

    // assert
    assertThat(subject.getAndClearHistory()).containsExactly(traceUnit);
  }

  @Test
  void WHEN_add_WITH_limitReached_THEN_firstMessageRemoved() {
    // arrange
    TraceUnit traceUnitFirst = new TraceUnit();
    traceUnitFirst.setExchangeId("id");
    TraceUnit traceUnitOther = new TraceUnit();
    // act
    subject.add(traceUnitFirst);
    for (int i = 0; i < 5; i++) {
      traceUnitOther.setExchangeId("id" + i);
      subject.add(traceUnitOther);
    }
    List<TraceUnit> target = subject.getAndClearHistory();

    // assert
    assertThat(target).doesNotContain(traceUnitFirst);
    assertThat(target).isNotEmpty();
  }

  @Test
  void WHEN_getAndClearHistoryConsecutively_THEN_historyEmptyAfterwards() {
    // arrange
    TraceUnit traceUnit = new TraceUnit();

    // act
    subject.add(traceUnit);
    subject.getAndClearHistory();

    // assert
    assertThat(subject.getAndClearHistory()).isEmpty();
  }
}
