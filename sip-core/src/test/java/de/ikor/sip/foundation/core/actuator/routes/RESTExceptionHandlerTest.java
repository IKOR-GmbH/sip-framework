package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RESTExceptionHandlerTest {

  RESTExceptionHandler subject;

  @BeforeEach
  void setUp() {
    subject = new RESTExceptionHandler();
  }

  @Test
  void When_IncompatibleOperationExceptionIsThrown_Expect_BadRequestStatus() {
    // act
    ResponseEntity<Object> responseEntity =
        subject.handle(new IncompatibleOperationException("any"), null, null);
    // assert
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
