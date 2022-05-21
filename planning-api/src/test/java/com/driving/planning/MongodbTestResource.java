package com.driving.planning;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

public class MongodbTestResource implements QuarkusTestResourceLifecycleManager {

    private MongoDBContainer mongoDBContainer;

    @Override
    public Map<String, String> start() {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.10"));
        mongoDBContainer.start();
        Map<String, String> map = new HashMap<>();
        map.put("quarkus.mongodb.connection-string", mongoDBContainer.getReplicaSetUrl());
        return map;
    }

    @Override
    public void stop() {
        if (mongoDBContainer != null){
            mongoDBContainer.stop();
        }
    }
}
