package com.driving.planning.config.cache.interceptor;

import com.driving.planning.config.cache.CachePut;
import com.hazelcast.map.IMap;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@CachePut(cacheName = "")
@Interceptor
@Priority(value = Interceptor.Priority.APPLICATION + 2)
public class CachePutInterceptor extends BaseInterceptor{

    @Override
    protected Object proceed(InvocationContext invocationContext) throws Exception {
        var annotation = invocationContext.getMethod().getAnnotation(CachePut.class);
        var cacheName = getCacheName(annotation.cacheName());
        IMap<String, Object> map = getMap(cacheName);
        Object obj = invocationContext.proceed();
        map.put(generateKey(invocationContext.getMethod(), invocationContext.getParameters()), obj);
        return obj;
    }

}
