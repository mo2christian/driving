package com.driving.planning.config.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class DatabaseResolver {

    private final MongoClient mongoClient;

    @Inject
    public DatabaseResolver(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Produces
    @RequestScoped
    public MongoDatabase resolve(Tenant tenant) {
        return mongoClient.getDatabase(tenant.getName());
    }

}

