package com.driving.planning.config.database;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.RuntimeType.SERVER;

@Provider
@ConstrainedTo(SERVER)
@ApplicationScoped
public class TenantInterceptor implements ContainerRequestFilter {

    @Inject
    Tenant tenant;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        var tenantName = requestContext.getHeaderString("x-app-tenant");
        if (tenantName != null){
            tenant.setName(tenantName);
        }
    }
}

