package de.ikor.sip.foundation.core.framework.beans;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExchangeScope implements Scope {

    public static final String SCOPE_PROPERTY = "exchange";
    private static final String REFERENCE = "exchange";

    protected final Map<String, Object> scope = new HashMap<>();
    protected final Class<? extends DefaultScopeDestructionCallback> defaultDestructionCallback = DefaultScopeDestructionCallback.class;
    protected final ObjectFactory<?> mapFactory = HashMap::new;

    @Override
    public String getConversationId() {
        if (getScopeContext() == null) return "";
        String id = ((Exchange) getScopeContext().get()).getProperty(SCOPE_PROPERTY).toString();
        log.debug("Scope Bound with Conversation from Exchange w/ scope id - {}",id);
        return id;
    }

    @Override
    public Object resolveContextualObject(String name) {
        return REFERENCE.equals(name);
    }

    protected ExchangeAttributes getScopeContext() {
        return ExchangeContextHolder.instance().getContext();
    }

    public Object get(String name, ObjectFactory<?> factory) {
        log.debug("Retrieving bean {}",name);
        Map<String, Object> beans = (Map<String, Object>) getScoped(scope,getConversationId(), mapFactory,false);
        return getScoped(beans,name,factory,true);
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        log.debug("Registering destruction callback to bean {}",name);
        getScopeContext().registerDestructionCallback(name, callback);
    }

    public Object remove(String name) {
        log.debug("Removing bean {}",name);
        Map<String, Object> beans = (Map<String, Object>) getScoped(scope,getConversationId(), mapFactory,false);
        return beans.remove(name);
    }

    protected Object getScoped(Map<String, Object> map, String name, ObjectFactory<?> factory, boolean registerCb) {
        Object o = map.get(name);
        if(o == null) {
            o = factory.getObject();
            map.put(name, o);

            if(registerCb && defaultDestructionCallback != null) {
                try {
                    Constructor<? extends DefaultScopeDestructionCallback> c = defaultDestructionCallback.getConstructor(Scope.class,String.class);
                    registerDestructionCallback(name, c.newInstance(this,name));
                } catch(Exception e) {
                    log.error("Could not setup destruction callback: " + name, e);
                }
            }
        }
        return o;
    }
}
