package com.driving.planning.config.cache.interceptor;

import com.driving.planning.config.cache.CacheKey;
import com.driving.planning.config.database.Tenant;
import com.hazelcast.client.HazelcastClientNotActiveException;
import com.hazelcast.client.HazelcastClientOfflineException;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class BaseInterceptor {

    @Inject
    Tenant tenant;

    @Inject
    HazelcastInstance hazelcastInstance;

    @Inject
    Logger logger;

    @AroundInvoke
    public final Object intercept(InvocationContext invocationContext) throws Exception{
        try{
            return proceed(invocationContext);
        }
        catch(HazelcastClientNotActiveException | HazelcastClientOfflineException | HazelcastException ex){
            logger.error("Error while connecting to hazelcast", ex);
            return invocationContext.proceed();
        }
    }

    protected abstract Object proceed(InvocationContext invocationContext) throws Exception;

    protected String getCacheName(String name){
        return tenant.getName() + "-" + name;
    }

    protected IMap<String, Object> getMap(String name){
        return hazelcastInstance.getMap(name);
    }

    protected String generateKey(Method method, Object[] parameters){
        var noCacheKey = true;
        var key = new StringBuilder("P");
        for (var i = 0; i < method.getParameterCount(); i++){
            if (method.getParameters()[i].isAnnotationPresent(CacheKey.class)){
                noCacheKey = false;
                key.append(parameters[i] == null ? "NULL" : parameters[i].toString());
            }
        }
        if (noCacheKey){
            return Arrays.stream(parameters)
                    .map(o -> o == null ? "NULL" : o.toString())
                    .reduce("P",(s1, s2) -> s1 + s2);
        }
        return key.toString();
    }

}
