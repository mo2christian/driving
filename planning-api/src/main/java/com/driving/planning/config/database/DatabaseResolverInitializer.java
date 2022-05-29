package com.driving.planning.config.database;

import com.driving.planning.account.domain.Account;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@ApplicationScoped
public class DatabaseResolverInitializer {

    @Inject
    Tenant tenant;

    public void init(@Observes StartupEvent event){
        alterAnnotation(Account.class, tenant);
    }

    private void alterAnnotation(Class clazzToLookFor, Tenant tenant){
        final String ANNOTATION_DATA = "annotationData";
        final String ANNOTATIONS = "annotations";
        try {
            Method method = Class.class.getDeclaredMethod(ANNOTATION_DATA, null);
            method.setAccessible(true);
            Object annotationData = method.invoke(clazzToLookFor);
            Field annotations = annotationData.getClass().getDeclaredField(ANNOTATIONS);
            annotations.setAccessible(true);
            Map<Class<? extends Annotation>, Annotation> map =
                    (Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);
            map.put(MongoEntity.class, new DynamicMongoEntity((MongoEntity) clazzToLookFor.getDeclaredAnnotation(MongoEntity.class), tenant));
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    public static class DynamicMongoEntity implements MongoEntity{

        private final MongoEntity mongoEntity;
        private final Tenant tenant;

        public DynamicMongoEntity(MongoEntity mongoEntity, Tenant tenant) {
            this.mongoEntity = mongoEntity;
            this.tenant = tenant;
        }

        @Override
        public String collection() {
            return mongoEntity.collection();
        }

        @Override
        public String database() {
            return tenant.getName();
        }

        @Override
        public String clientName() {
            return mongoEntity.clientName();
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return mongoEntity.annotationType();
        }
    }

}
