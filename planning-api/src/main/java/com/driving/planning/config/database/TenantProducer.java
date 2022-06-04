package com.driving.planning.config.database;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class TenantProducer {

    @Produces
    @RequestScoped
    public Tenant get(){
        return new Tenant("base");
    }

}
