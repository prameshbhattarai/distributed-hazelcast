package org.distributed.resource;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.distributed.cache.CacheConfig;
import org.distributed.config.Setting;
import org.distributed.dto.ServerResponse;
import org.distributed.handler.AsyncCompletionHandler;
import org.glassfish.jersey.server.ContainerRequest;

import java.util.concurrent.CompletableFuture;

@Path("/api")
public class ServerBResource {
    private static final Logger logger = LogManager.getLogger(ServerBResource.class);
    private final HazelcastInstance cacheInstance = CacheConfig.getHazelcastInstance();
    private final IMap<String, String> cache = cacheInstance.getMap(Setting.CACHE_GROUP);

    @GET
    @Path("/ping")
    public void ping(@Suspended AsyncResponse asyncResponse,
                     @Context ContainerRequest request) {
        logger.info("serverB ping request received");

        var futureResponse = new CompletableFuture<Response>();
        futureResponse.complete(Response.ok()
                .entity(new ServerResponse("pong from server B !!!"))
                .build());

        AsyncCompletionHandler.handle(
                request,
                asyncResponse,
                futureResponse
        );
    }

    @PUT
    @Path("/cache/{key}/value/{value}")
    public void addToCache(@Suspended AsyncResponse asyncResponse,
                           @PathParam("key") String key,
                           @PathParam("value") String value,
                           @Context ContainerRequest request) {
        logger.info("ServerB:: add to cache request received");

        var futureResponse = CompletableFuture.supplyAsync(() -> {
            if (key == null || key.isEmpty()) {
                return Response.ok()
                        .entity(new ServerResponse("Key cannot be null or empty"))
                        .build();
            }

            if (value == null || value.isEmpty()) {
                return Response.ok()
                        .entity(new ServerResponse("Value cannot be null or empty"))
                        .build();
            }

            CacheConfig.printCacheLocation(key);

            var val = cache.get(key);

            if (val != null) {
                var message = "Value %s already exist for key %s".formatted(val, key);
                logger.info(message);
                return Response.ok()
                        .entity(new ServerResponse(message))
                        .build();
            }

            cache.put(key, value);
            var message = "Value %s store for key %s".formatted(value, key);
            logger.info(message);
            return Response.ok()
                    .entity(new ServerResponse(message))
                    .build();
        });
        AsyncCompletionHandler.handle(
                request,
                asyncResponse,
                futureResponse
        );
    }

    @GET
    @Path("/cache/{key}")
    public void getFromCache(@Suspended AsyncResponse asyncResponse,
                             @PathParam("key") String key,
                             @Context ContainerRequest request) {
        logger.info("ServerB:: get from cache request received");

        var futureResponse = CompletableFuture.supplyAsync(() -> {
            if (key == null || key.isEmpty()) {
                return Response.ok()
                        .entity(new ServerResponse("Key cannot be null or empty"))
                        .build();
            }

            CacheConfig.printCacheLocation(key);

            var val = cache.get(key);

            if (val == null) {
                var message = "No value found for key %s".formatted(key);
                logger.info(message);
                return Response.ok()
                        .entity(new ServerResponse(message))
                        .build();
            }

            var message = "Value %s store for key %s".formatted(val, key);
            logger.info(message);
            return Response.ok()
                    .entity(new ServerResponse(message))
                    .build();
        });
        AsyncCompletionHandler.handle(
                request,
                asyncResponse,
                futureResponse
        );
    }
}
