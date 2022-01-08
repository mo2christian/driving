package com.driving.planning;

import io.quarkus.test.hazelcast.HazelcastServerTestResource;

import java.util.HashMap;
import java.util.Map;

public class HazelcastTestResource extends HazelcastServerTestResource {

    @Override
    public Map<String, String> start() {
        Map<String, String> map = new HashMap<>(super.start());
        map.put("quarkus.hazelcast-client.cluster-members", "localhost");
        return map;
    }
}
