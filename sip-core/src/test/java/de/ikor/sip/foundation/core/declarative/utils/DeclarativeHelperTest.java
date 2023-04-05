package de.ikor.sip.foundation.core.declarative.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelperTestModels.ExceptionThrowingConstructorMapper;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelperTestModels.MultipleMethodsMapper;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelperTestModels.MyExtendedIntegerList;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelperTestModels.NoArgsConstructorMapper;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.junit.jupiter.api.Test;

class DeclarativeHelperTest {

  @Test
  void WHEN_annotationNotPresent_THEN_throwSIPFrameworkException() {
    assertThatThrownBy(
            () -> {
              DeclarativeHelper.getAnnotationOrThrow(Test.class, new Random());
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "Annotation @%s required on class %s", Test.class.getSimpleName(), Random.class);
  }

  @Test
  void WHEN_createMapperWithoutNoArgConstructor_THEN_throwSIPException() {

    assertThatThrownBy(
            () -> {
              DeclarativeHelper.createMapperInstance(NoArgsConstructorMapper.class);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "Mapper %s needs to have a no-arg constructor, please define one.",
            NoArgsConstructorMapper.class.getName());
  }

  @Test
  void WHEN_createMapperWhichThrowsError_THEN_throwSIPExceptionWhichWrapsRuntimeException() {

    assertThatThrownBy(
            () -> {
              DeclarativeHelper.createMapperInstance(ExceptionThrowingConstructorMapper.class);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "SIP couldn't create a Mapper %s.", ExceptionThrowingConstructorMapper.class.getName())
        .hasCauseInstanceOf(InvocationTargetException.class)
        .hasStackTraceContaining("Exception message");
  }

  @Test
  void WHEN_mapperHasMoreThanOneMappingMethod_THEN_throwSIPException() {
    assertThatThrownBy(
            () -> {
              DeclarativeHelper.getMappingMethod(MultipleMethodsMapper.class);
            })
        .isInstanceOf(SIPFrameworkInitializationException.class)
        .hasMessage(
            "Failed to automatically resolve the model classes for the Mapper: %s. Please @Override the getSourceModelClass() and getTargetModelClass() methods",
            MultipleMethodsMapper.class.getName());
  }

  @Test
  void WHEN_usingJMSEndpoint_THEN_bridgeErrorHandlerIsFalse() {
    // arrange
    EndpointConsumerBuilder jmsEndpoint = StaticEndpointBuilders.jms("no-queue");

    // act
    EndpointConsumerBuilder endpointConsumerBuilder =
        DeclarativeHelper.resolveForbiddenEndpoint(jmsEndpoint);

    // assert
    assertThat(endpointConsumerBuilder.getUri()).contains("bridgeErrorHandler=false");
  }

  @Test
  void WHEN_usingClassWithGenericSuperclass_THEN_resolveGeneric() {

    // act
    Class<?> genericClass =
        DeclarativeHelper.getClassFromGeneric(MyExtendedIntegerList.class, ArrayList.class);

    // assert
    assertThat(genericClass.getTypeName()).isEqualTo("java.lang.Integer");
  }
}
