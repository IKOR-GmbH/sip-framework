package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import static de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.SIPJmsTextMessage.*;
import static org.apache.camel.component.jms.JmsConstants.JMS_X_GROUP_ID;
import static org.assertj.core.api.Assertions.*;

import javax.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SIPJmsTextMessageTest {

  private static final String TEST = "test";
  private static final Object NULL = null;

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

  @Test
  void GIVEN_noInputs_WHEN_callingNonUsableGettersAndSetters_THEN_expectDefaultValues() {
    // act & assert
    assertThat(subject.propertyExists(TEST)).isFalse();
    assertThat(subject.getBooleanProperty(TEST)).isFalse();
    assertThat(subject.getByteProperty(TEST)).isZero();
    assertThat(subject.getShortProperty(TEST)).isZero();
    assertThat(subject.getIntProperty(TEST)).isZero();
    assertThat(subject.getLongProperty(TEST)).isZero();
    assertThat(subject.getFloatProperty(TEST)).isZero();
    assertThat(subject.getDoubleProperty(TEST)).isZero();
    assertThat(subject.getJMSDeliveryTime()).isZero();
    assertThat(subject.getBody(Object.class)).isNull();
    assertThat(subject.isBodyAssignableTo(null)).isFalse();
  }

  @Test
  void GIVEN_keyWithNullValue_WHEN_getStringProperty_THEN_expectNullValue() {
    // act & assert
    assertThat(subject.getStringProperty(JMS_MESSAGE_ID)).isNull();
  }

  @Test
  void GIVEN_keyWithStringValue_WHEN_getStringProperty_THEN_expectStringValue() {
    // arrange
    subject.setObjectProperty("testKey", TEST);
    // act & assert
    assertThat(subject.getStringProperty("testKey")).isEqualTo(TEST);
  }
}
