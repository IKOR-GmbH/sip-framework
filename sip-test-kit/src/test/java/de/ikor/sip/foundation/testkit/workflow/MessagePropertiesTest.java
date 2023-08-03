package de.ikor.sip.foundation.testkit.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.ikor.sip.foundation.testkit.configurationproperties.models.MessageProperties;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;

class MessagePropertiesTest {
  MessageProperties subject = new MessageProperties();

  @Test
  void ifBodyIsFile_thenBodyIsReadFromFile() {
    subject.setBody("file:body.json");
    FileNotFoundException fileNotFoundException =
        assertThrows(FileNotFoundException.class, () -> subject.getBody());
    assertThrows(FileNotFoundException.class, () -> subject.getBody());
    assertThat(fileNotFoundException.getMessage()).contains("body.json");
  }
}
