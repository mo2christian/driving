package com.driving.planning.config.cache.interceptor;

import com.driving.planning.config.cache.Cacheable;
import com.hazelcast.map.IMap;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Cacheable(cacheName = "")
@Interceptor
@Priority(value = Interceptor.Priority.APPLICATION + 2)
public class CacheableInterceptor extends BaseInterceptor {

    @Override
    public Object proceed(InvocationContext invocationContext) throws Exception{
        var cacheable = invocationContext.getMethod().getAnnotation(Cacheable.class);
        var cacheName = getCacheName(cacheable.cacheName());
        IMap<String, Object> map = getMap(cacheName);
        String key = generateKey(invocationContext.getMethod(), invocationContext.getParameters());
        Object result = map.get(key);
        if (result != null){
            return result;
        }
        result = invocationContext.proceed();
        map.put(key, result);
        return result;
    }


}
