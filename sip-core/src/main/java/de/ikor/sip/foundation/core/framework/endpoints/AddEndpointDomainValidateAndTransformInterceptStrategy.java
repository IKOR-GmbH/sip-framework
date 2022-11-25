package de.ikor.sip.foundation.core.framework.endpoints;

import lombok.SneakyThrows;
import org.apache.camel.*;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.camel.support.processor.DelegateAsyncProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static de.ikor.sip.foundation.core.proxies.ProcessorProxy.NON_OUTGOING_PROCESSOR_PREFIXES;

@Component
public class AddEndpointDomainValidateAndTransformInterceptStrategy implements InterceptStrategy, Ordered {

    @Override
    public Processor wrapProcessorInInterceptors(CamelContext context, NamedNode definition, Processor target, Processor nextTarget) {
        if (target instanceof EndpointAware && isOutEndpoint(target) && !isInMemoryComponent(target)) {
            return new DelegateAsyncProcessor(exchange -> doWrappedProcessing(target, exchange));
        }
        return target;
    }

    private void doWrappedProcessing(Processor target, Exchange exchange) {
        OutEndpoint endpoint = (OutEndpoint) ((EndpointAware) target).getEndpoint();
        endpoint.getDomainClassType().ifPresent(domainClassType -> domainValidationProcessing(domainClassType, exchange, endpoint.getEndpointUri()));
        endpoint.getTransformFunction().ifPresent(function -> domainTransformationProcessing(function, exchange));
        targetProcessorProcessing(target, exchange);
    }

    private boolean isOutEndpoint(Processor target) {
        return ((EndpointAware) target).getEndpoint() instanceof OutEndpoint;
    }

    private boolean isInMemoryComponent(Processor target) {
        return StringUtils.startsWithAny(((EndpointAware) target).getEndpoint().getEndpointUri(), NON_OUTGOING_PROCESSOR_PREFIXES);
    }

    @SneakyThrows
    private void domainValidationProcessing(Class<?> domainClassType, Exchange exchange, String endpointUri) {
        Processor validationProcessor = new EndpointDomainValidation(domainClassType, endpointUri);
        validationProcessor.process(exchange);
    }

    @SneakyThrows
    private void domainTransformationProcessing(Function<?, ?> function, Exchange exchange) {
        Processor transformationProcessor = new EndpointDomainTransformation<>(function);
        transformationProcessor.process(exchange);
    }

    @SneakyThrows
    private void targetProcessorProcessing(Processor target, Exchange exchange) {
        target.process(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
