package de.ikor.sip.foundation.core.framework.stubs;

import lombok.Data;

@Data
public class OutEndpointDomain {

    private String contentWithMessage;

    public OutEndpointDomain(String content) {
        this.contentWithMessage = String.format("%s success", content);
    }
}
