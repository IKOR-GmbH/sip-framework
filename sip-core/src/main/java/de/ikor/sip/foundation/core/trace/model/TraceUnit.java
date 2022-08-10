package de.ikor.sip.foundation.core.trace.model;

import lombok.Data;
import org.apache.camel.ExchangePattern;

import java.util.HashMap;
import java.util.Map;

@Data
public class TraceUnit {
    private String nodeId;
    private String uri;
    private String exchangeId;
    private ExchangePattern exchangePattern;
    private Map<String, Object> properties = new HashMap<>();
    private Map<String, Object> internalProperties = new HashMap<>();
    private Map<String, Object> headers = new HashMap<>();
    private String bodyType;
    private String body;
    private String caughtExceptionType;
    private String caughtExceptionMessage;
    private String exceptionType;
    private String exceptionMessage;
    private String stackTrace;
    private ExchangeTracePoint tracePoint = ExchangeTracePoint.ONGOING;
}
