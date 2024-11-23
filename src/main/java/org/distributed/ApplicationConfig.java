package org.distributed;

import org.distributed.exceptionhandler.GenericExceptionMapper;
import org.distributed.mapper.ObjectMapper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;

public class ApplicationConfig {
    public final ResourceConfig app;

    public ApplicationConfig() {
        this.app = new ResourceConfig();
    }

    public ApplicationConfig registerDefaultMappers() {
        this.app.registerClasses(GenericExceptionMapper.class);
        EncodingFilter.enableFor(this.app, GZipEncoder.class);
        return this;
    }

    public ApplicationConfig name(String name) {
        this.app.setApplicationName(name);
        return this;
    }

    public ApplicationConfig enableJackson() {
        var jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(ObjectMapper.OBJECT_MAPPER);
        this.app.registerInstances(jacksonProvider);
        return this;
    }

    public ResourceConfig build() {
        return this.app;
    }
}