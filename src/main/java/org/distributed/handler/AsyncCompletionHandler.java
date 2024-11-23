package org.distributed.handler;

import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ContainerRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class AsyncCompletionHandler {
    public static final Logger logger = LogManager.getLogger(AsyncCompletionHandler.class);
    public static final int TIMEOUT_SECONDS = 25;

    private AsyncCompletionHandler() {
    }

    public static void handle(
            ContainerRequest request,
            AsyncResponse response,
            CompletableFuture<Response> completableFuture
    ) {
        response.register(new DisconnectHandler(completableFuture));
        response.setTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        response.setTimeoutHandler(r -> {
            r.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
            completableFuture.cancel(true);
            logger.warn("Request timed out {}", request);
        });

        completableFuture.whenComplete(
            (attempt, exception) -> {
                if (exception != null) {
                    if (!response.isSuspended())
                        logger.error("Unreported error {}", request, exception);
                    if (exception instanceof CompletionException)
                        response.resume(exception.getCause());
                    else
                        response.resume(exception);
                } else
                    response.resume(attempt);
            });
    }
}
