package com.driving.planning.config.cache.interceptor;

import com.driving.planning.config.cache.CacheEvict;
import com.hazelcast.map.IMap;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@CacheEvict(cacheName = "")
@Interceptor
@Priority(value = Interceptor.Priority.APPLICATION + 2)
public class CacheEvictInterceptor extends BaseInterceptor{

    @Override
    protected Object proceed(InvocationContext invocationContext) throws Exception {
        var annotation = invocationContext.getMethod().getAnnotation(CacheEvict.class);
        var cacheName = getCacheName(annotation.cacheName());
        IMap<String, Object> map = getMap(cacheName);
        Object obj = invocationContext.proceed();
        map.clear();
        return obj;
    }

}
