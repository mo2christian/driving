package com.driving.planning.config.logging;

import io.quarkiverse.loggingjson.JsonGenerator;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.Level;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.io.IOException;

import static org.mockito.Mockito.*;

@QuarkusTest
class JsonLoggingProviderTest {

    @Inject
    JsonLoggingProvider provider;

    @Test
    void writeTo() throws IOException {
        JsonGenerator generator = mock(JsonGenerator.class);
        ExtLogRecord event = new ExtLogRecord(Level.INFO, "message", "class");
        final var span = "span";
        event.putMdc("spanId", span);
        final var trace = "trace";
        event.putMdc("traceId", trace);
        provider.projectId = "project";
        provider.writeTo(generator, event);
        ArgumentCaptor<String> keys = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> values = ArgumentCaptor.forClass(String.class);
        verify(generator, times(3)).writeStringField(keys.capture(), values.capture());
        Assertions.assertThat(keys.getAllValues())
                .hasSize(3)
                .contains("severity", "logging.googleapis.com/trace", "logging.googleapis.com/spanId");
        Assertions.assertThat(values.getAllValues())
                .hasSize(3)
                .contains("INFO", String.format("projects/%s/traces/%s", provider.projectId, trace), span);
    }

    @Test
    void writoWithError() throws IOException {
        JsonGenerator generator = mock(JsonGenerator.class);
        ExtLogRecord event = new ExtLogRecord(Level.ERROR, "message", "class");
        event.setThrown(new RuntimeException("TESTER"));
        final var span = "span";
        event.putMdc("spanId", span);
        final var trace = "trace";
        event.putMdc("traceId", trace);
        provider.projectId = "project";
        provider.writeTo(generator, event);
        ArgumentCaptor<String> keys = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> values = ArgumentCaptor.forClass(String.class);
        verify(generator, times(4)).writeStringField(keys.capture(), values.capture());
        Assertions.assertThat(keys.getAllValues())
                .hasSize(4)
                .contains("message", "logging.googleapis.com/trace", "logging.googleapis.com/spanId", "severity");
        Assertions.assertThat(values.getAllValues())
                .hasSize(4)
                .element(3)
                .matches(s -> s.contains("TESTER"));
    }

}
