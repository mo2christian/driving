package com.driving.planning.config.cache;

import com.driving.planning.HazelcastTestResource;
import com.driving.planning.ZipkinTestResource;
import com.driving.planning.config.database.Tenant;
import com.hazelcast.client.HazelcastClientNotActiveException;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@QuarkusTest
@QuarkusTestResource(value = HazelcastTestResource.class)
@QuarkusTestResource(value = ZipkinTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CacheTest {

    @Inject
    HelloDecorator helloDecorator;

    @InjectSpy
    HelloService helloService;

    @InjectMock
    Tenant tenant;

    @BeforeEach
    public void init(){
        when(tenant.getName()).thenReturn("base");
        reset(helloService);
    }

    @Test
    @Order(1)
    void testCacheable(){
        assertThat(helloDecorator.cacheable()).isEqualTo("C");
        verify(helloService, times(1)).cacheable();
        reset(helloService);

        assertThat(helloDecorator.cacheable()).isEqualTo("C");
        verify(helloService, never()).cacheable();
    }

    @Test
    @Order(2)
    void testCacheableKey(){
        var s = "S";
        assertThat(helloDecorator.cacheableKey(s)).isEqualTo(s);
        Mockito.verify(helloService, times(1)).cacheableKey(s);
        Mockito.reset(helloService);

        assertThat(helloDecorator.cacheableKey(s)).isEqualTo(s);
        Mockito.verify(helloService, never()).cacheableKey(anyString());
    }

    @Test
    @Order(3)
    void testCacheableKeyAnno(){
        assertThat(helloDecorator.cacheableAnnoKey("A", null)).isEqualTo("A");
        Mockito.verify(helloService, times(1)).cacheableKey("A");
        Mockito.reset(helloService);

        assertThat(helloDecorator.cacheableKey("A")).isEqualTo("A");
        Mockito.verify(helloService, never()).cacheableKey(anyString());

        assertThat(helloDecorator.cacheableAnnoKey("B", null)).isEqualTo("B");
        Mockito.verify(helloService, times(1)).cacheableKey("B");
        Mockito.reset(helloService);

        assertThat(helloDecorator.cacheableAnnoKey("A", null)).isEqualTo("A");
        Mockito.verify(helloService, never()).cacheableKey(anyString());
    }

    @Test
    @Order(4)
    void testPut(){
        assertThat(helloDecorator.put()).isEqualTo("P");
        verify(helloService, times(1)).put();
        reset(helloService);

        assertThat(helloDecorator.cacheable()).isEqualTo("P");
        verify(helloService, never()).cacheable();
        helloDecorator.evict();
    }

    @Test
    @Order(5)
    void testError(){
        doThrow(new HazelcastClientNotActiveException()).when(helloService).error();
        assertThatThrownBy(() -> helloDecorator.error()).isInstanceOf(HazelcastClientNotActiveException.class);
        verify(helloService, times(2)).error();
    }

}
