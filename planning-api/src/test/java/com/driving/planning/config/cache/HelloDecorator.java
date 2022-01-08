package com.driving.planning.config.cache;

import com.hazelcast.client.HazelcastClientNotActiveException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HelloDecorator {

    @Inject
    HelloService helloService;

    @Cacheable(cacheName = "cache")
    public String cacheable(){
        return helloService.cacheable();
    }

    @Cacheable(cacheName = "cache")
    public String cacheableKey(String s){
        return helloService.cacheableKey(s);
    }

    @Cacheable(cacheName = "cache")
    public String cacheableAnnoKey(@CacheKey String s1, String s2){
        return helloService.cacheableKey(s1);
    }

    @CachePut(cacheName = "cache")
    public String put(){
        return helloService.put();
    }

    @CacheEvict(cacheName = "cache")
    public void evict(){
    }

    @CacheEvict(cacheName = "cache")
    public void error(){
        helloService.error();
    }
}
