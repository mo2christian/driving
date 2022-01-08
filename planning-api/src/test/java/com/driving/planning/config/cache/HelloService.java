package com.driving.planning.config.cache;

import com.hazelcast.client.HazelcastClientNotActiveException;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HelloService {

    public String cacheable(){
        return "C";
    }

    public String cacheableKey(String s){
        return s;
    }

    public String put(){
        return "P";
    }

    public void error(){
    }

}
