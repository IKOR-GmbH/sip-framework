package de.ikor.sip.foundation.core.trace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


// TODO update tests
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "sip.core.tracing.limit=5")
@ContextConfiguration(classes = {TraceHistory.class})
class TraceHistoryTest {

//  @Autowired private TraceHistory subject;
//
//  @Test
//  void WHEN_add_WITH_message_THEN_messageAdded() {
//    // arrange
//    String message = UUID.randomUUID().toString();
//
//    // act
//    subject.add(message);
//
//    // assert
//    assertThat(subject.getAndClearHistory()).containsExactly(message);
//  }
//
//  @Test
//  void WHEN_add_WITH_limitReached_THEN_firstMessageRemoved() {
//    // act
//    for (int i = 0; i < 6; i++) {
//      subject.add("message" + i);
//    }
//
//    // assert
//    assertThat(subject.getAndClearHistory()).doesNotContain("message0");
//  }
//
//  @Test
//  void WHEN_getAndClearHistoryConsecutively_THEN_historyEmptyAfterwards() {
//    // arrange
//    String message = UUID.randomUUID().toString();
//
//    // act
//    subject.add(message);
//    subject.getAndClearHistory();
//
//    // assert
//    assertThat(subject.getAndClearHistory()).isEmpty();
//  }
}
