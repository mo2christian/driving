package com.driving.planning.config.zipkin;

import io.smallrye.config.ConfigMapping;

import java.util.Optional;

@ConfigMapping(prefix = "app.zipkin")
public interface ZipkinConfig {

    String endpoint();

    String serviceName();

    Optional<Float> samplerParam();
}
