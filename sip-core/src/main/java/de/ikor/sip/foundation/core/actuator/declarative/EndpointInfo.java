package de.ikor.sip.foundation.core.actuator.declarative;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointInfo {

    private String id;
    private String camelUri;
}
