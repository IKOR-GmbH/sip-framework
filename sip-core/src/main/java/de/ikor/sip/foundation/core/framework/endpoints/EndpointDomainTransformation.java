package de.ikor.sip.foundation.core.framework.endpoints;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.function.Function;

@Slf4j
@AllArgsConstructor
public class EndpointDomainTransformation<T, D> implements Processor {

    private Function<T, D> transformFunction;

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            exchange.getMessage().setBody(transformFunction.apply((T) exchange.getMessage().getBody()));
        } catch(ClassCastException e) {
            log.error(String.format("Class casting error in class %s", transformFunction.getClass()));
        }

    }
}
