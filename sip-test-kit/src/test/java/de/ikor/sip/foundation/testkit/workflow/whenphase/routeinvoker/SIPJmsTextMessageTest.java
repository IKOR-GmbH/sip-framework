package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import static de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.SIPJmsTextMessage.*;
import static org.apache.camel.component.jms.JmsConstants.JMS_X_GROUP_ID;
import static org.assertj.core.api.Assertions.*;

import javax.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SIPJmsTextMessageTest {

  private SIPJmsTextMessage subject;

  @BeforeEach
  void setup() {
    subject = new SIPJmsTextMessage(null);
  }

  @Test
  void GIVEN_noInputs_WHEN_callingGetters_THEN_expectDefaultValues() {
    // act & assert
    assertThat(subject.getText()).isNull();
    assertThat(subject.getJMSCorrelationID()).isNull();
    assertThat(subject.getJMSCorrelationIDAsBytes()).isNull();
    assertThat(subject.getJMSDestination()).isNull();
    assertThat(subject.getJMSMessageID()).isNull();
    assertThat(subject.getJMSRedelivered()).isFalse();
    assertThat(subject.getJMSReplyTo()).isNull();
    assertThat(subject.getJMSType()).isNull();
    assertThat(subject.getObjectProperty(JMS_X_GROUP_ID)).isNull();
    assertThat(subject.getObjectProperty(JMS_X_USER_ID)).isNull();
    assertThat(subject.getJMSDeliveryMode()).isEqualTo(Message.DEFAULT_DELIVERY_MODE);
    assertThat(subject.getJMSExpiration()).isEqualTo(Message.DEFAULT_TIME_TO_LIVE);
    assertThat(subject.getJMSPriority()).isEqualTo(Message.DEFAULT_PRIORITY);
    assertThat(subject.getJMSTimestamp()).isLessThanOrEqualTo(System.currentTimeMillis());
  }

  @Test
  void GIVEN_overridingInputs_WHEN_callingGetters_THEN_expectInputValues() {
    // arrange
    subject.setText("text value");
    subject.setJMSCorrelationID("123");
    subject.setJMSMessageID("123435");
    subject.setJMSRedelivered(true);
    subject.setJMSType("message");
    subject.setObjectProperty(JMS_X_GROUP_ID, "10");
    subject.setObjectProperty(JMS_X_USER_ID, "10");
    subject.setJMSDeliveryMode(1);
    subject.setJMSExpiration(200);
    subject.setJMSPriority(6);

    // act & assert
    assertThat(subject.getText()).isEqualTo("text value");
    assertThat(subject.getJMSCorrelationID()).isEqualTo("123");
    assertThat(subject.getJMSMessageID()).isEqualTo("123435");
    assertThat(subject.getJMSRedelivered()).isTrue();
    assertThat(subject.getJMSType()).isEqualTo("message");
    assertThat(subject.getObjectProperty(JMS_X_GROUP_ID)).isEqualTo("10");
    assertThat(subject.getObjectProperty(JMS_X_USER_ID)).isEqualTo("10");
    assertThat(subject.getJMSDeliveryMode()).isEqualTo(1);
    assertThat(subject.getJMSExpiration()).isEqualTo(200);
    assertThat(subject.getJMSPriority()).isEqualTo(6);
  }
}
