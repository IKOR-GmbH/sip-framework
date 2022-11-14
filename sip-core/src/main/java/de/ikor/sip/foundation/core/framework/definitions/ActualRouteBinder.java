package de.ikor.sip.foundation.core.framework.definitions;


import de.ikor.sip.foundation.core.framework.routers.RouteBinder;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class ActualRouteBinder extends RouteBinder {
    public ActualRouteBinder(String useCase, Class<?> centralModelRequest) {
        super(useCase, centralModelRequest, EMPTY);
    }
}
