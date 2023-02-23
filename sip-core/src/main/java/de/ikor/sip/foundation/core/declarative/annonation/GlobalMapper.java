package de.ikor.sip.foundation.core.declarative.annonation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * Annotation to be used on model mappers implementing the {@link
 * de.ikor.sip.foundation.core.declarative.model.ModelMapper} interface.
 *
 * <p>Mappers annotated with this annotation will be automatically registered and don't need to be
 * declared explicitly in the @{@link UseResponseMapper} and @{@link UseRequestMapper} annotation.
 *
 * @see UseResponseMapper
 * @see UseRequestMapper
 * @see de.ikor.sip.foundation.core.declarative.model.ModelMapper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface GlobalMapper {}
