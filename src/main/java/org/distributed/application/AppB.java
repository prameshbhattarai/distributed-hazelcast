package org.distributed.application;

import org.distributed.ApplicationConfig;
import org.distributed.handler.CORSFilter;
import org.distributed.resource.ServerBResource;
import org.glassfish.jersey.server.ResourceConfig;

public class AppB {
    public ResourceConfig server() {
        var app = new ApplicationConfig()
                .name("Jersey Async Server B")
                .enableJackson()
                .registerDefaultMappers()
                .build();

        // enable CORS request
        app.register(CORSFilter.class);

        // register API Resource instances
        app.registerInstances(new ServerBResource());
        return app;
    }
}
