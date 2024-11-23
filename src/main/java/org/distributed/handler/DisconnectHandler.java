package org.distributed.handler;


import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.ConnectionCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

public class DisconnectHandler implements ConnectionCallback {
    public static final Logger logger = LogManager.getLogger(DisconnectHandler.class);
    private final CompletableFuture<?> future;

    public DisconnectHandler(CompletableFuture<?> future) {
        this.future = future;
    }

    @Override
    public void onDisconnect(AsyncResponse disconnected) {
        if (!future.isDone()) {
            future.cancel(true);
            logger.info("Client disconnected before request completed");
        }
    }
}
