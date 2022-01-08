package com.driving.planning;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

public class ZipkinTestResource implements QuarkusTestResourceLifecycleManager {

    private GenericContainer<?> zipkinContainer;

    @Override
    public Map<String, String> start() {
        final int port = 9411;
        zipkinContainer = new GenericContainer<>(DockerImageName.parse("openzipkin/zipkin:2.23"))
            .withExposedPorts(port);
        zipkinContainer.start();
        Map<String, String> config = new HashMap<>();
        config.put("app.zipkin.endpoint", String.format("http://%s:%s/api/v2/spans", zipkinContainer.getContainerIpAddress(), zipkinContainer.getMappedPort(port)));
        return config;
    }

    @Override
    public void stop() {
        if (zipkinContainer != null){
            zipkinContainer.stop();
        }
    }
}
