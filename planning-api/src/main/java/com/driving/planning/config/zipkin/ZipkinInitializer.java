package com.driving.planning.config.zipkin;


import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.opentracing.BraveTracer;
import brave.propagation.B3Propagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.stackdriver.StackdriverTracePropagation;
import brave.sampler.Sampler;
import io.opentracing.util.GlobalTracer;
import io.quarkus.runtime.StartupEvent;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class ZipkinInitializer {

    @Inject
    ZipkinConfig zipkinConfig;

    public void tracer(@Observes StartupEvent event) {
        Sender sender = OkHttpSender.create(zipkinConfig.endpoint());
        ThreadLocalCurrentTraceContext traceContext = ThreadLocalCurrentTraceContext.newBuilder()
                .addScopeDecorator(MDCScopeDecorator.get())
                .build();
        AsyncZipkinSpanHandler zipkinSpanHandler = AsyncZipkinSpanHandler.newBuilder(sender).build();
        var sampler = Sampler.create(zipkinConfig.samplerParam().orElse(1f));
        var tracing = Tracing.newBuilder()
                .localServiceName(zipkinConfig.serviceName())
                .addSpanHandler(zipkinSpanHandler)
                .sampler(sampler)
                .currentTraceContext(traceContext)
                .propagationFactory(StackdriverTracePropagation.newFactory(B3Propagation.FACTORY))
                .traceId128Bit(true)
                .build();
        GlobalTracer.registerIfAbsent(BraveTracer.create(tracing));
    }
}
