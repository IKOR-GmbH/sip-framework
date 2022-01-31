package de.ikor.sip.foundation.core.trace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/** Configures bean "sipTraceTypeEnums" with trace types defined in "trace-type" property or all if * is set */
@Configuration
public class TraceTypeConfiguration {

    @Bean
    public Set<SIPTraceTypeEnum> sipTraceTypeEnums(SIPTraceConfig traceConfig){
        LinkedHashSet<SIPTraceTypeEnum> set = new LinkedHashSet<>();
        if(traceConfig.getTraceType().contains("*")){
            Arrays.stream(SIPTraceTypeEnum.values()).forEach(set::add);
        } else {
            traceConfig.getTraceType().forEach(name -> set.add(SIPTraceTypeEnum.valueOf(name)));
        }
        return set;
    }

}
