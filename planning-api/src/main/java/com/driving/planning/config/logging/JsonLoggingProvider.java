package com.driving.planning.config.logging;

import io.quarkiverse.loggingjson.JsonGenerator;
import io.quarkiverse.loggingjson.JsonProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logmanager.ExtLogRecord;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Singleton
public class JsonLoggingProvider implements JsonProvider {

    @ConfigProperty(name = "app.project_id")
    String projectId;

    @Override
    public void writeTo(JsonGenerator generator, ExtLogRecord event) throws IOException {
        generator.writeStringField("logging.googleapis.com/trace", String.format("projects/%s/traces/%s", projectId, event.getMdc("traceId")));
        generator.writeStringField("logging.googleapis.com/spanId", event.getMdc("spanId"));
        generator.writeStringField("severity", event.getLevel().getName());
        if (event.getThrown() != null){
            var writer = new StringWriter();
            event.getThrown().printStackTrace(new PrintWriter(writer));
            generator.writeStringField("message", writer.toString());
        }
    }
}
