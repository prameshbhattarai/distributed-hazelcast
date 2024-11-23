package org.distributed.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.distributed.dto.ServerResponse;
import org.distributed.handler.AsyncCompletionHandler;
import org.glassfish.jersey.server.ContainerRequest;

import java.util.concurrent.CompletableFuture;

@Path("/api")
public class ServerAResource {
    private static final Logger logger = LogManager.getLogger(ServerAResource.class);

    @GET
    @Path("/ping")
    public void ping(@Suspended AsyncResponse asyncResponse,
                         @Context ContainerRequest request) {
        logger.info("serverA ping request received");

        var futureResponse = new CompletableFuture<Response>();
        futureResponse.complete(Response.ok()
                .entity(new ServerResponse("pong from server A !!!"))
                .build());

        AsyncCompletionHandler.handle(
                request,
                asyncResponse,
                futureResponse
        );
    }
}
