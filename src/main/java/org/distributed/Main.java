package org.distributed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.distributed.application.AppA;
import org.distributed.application.AppB;
import org.distributed.application.AppC;
import org.distributed.config.Setting;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private static AppURLConfig createApplicationA() {
        var app = new AppA().server();
        var uri = URI.create(Setting.SERVER_A_URL);
        return new AppURLConfig(app, uri);
    }

    private static AppURLConfig createApplicationB() {
        var app = new AppB().server();
        var uri = URI.create(Setting.SERVER_B_URL);
        return new AppURLConfig(app, uri);
    }

    private static AppURLConfig createApplicationC() {
        var app = new AppC().server();
        var uri = URI.create(Setting.SERVER_C_URL);
        return new AppURLConfig(app, uri);
    }

    private static void addShutdownHooks(HttpServer... servers) {
        Arrays.stream(servers).forEach(server ->
                Runtime.getRuntime().addShutdownHook(
                        new Thread(
                                server::shutdownNow,
                                "serverShutdown %s".formatted(server.getClass())
                        )
                ));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        var appA = createApplicationA();
        var appB = createApplicationB();
        var appC = createApplicationC();

        var serverA = GrizzlyHttpServerFactory.createHttpServer(appA.serverURI, appA.app, false);
        serverA.getHttpHandler().setAllowEncodedSlash(true);
        serverA.start();

        var serverB = GrizzlyHttpServerFactory.createHttpServer(appB.serverURI, appB.app, false);
        serverB.getHttpHandler().setAllowEncodedSlash(true);
        serverB.start();

        var serverC = GrizzlyHttpServerFactory.createHttpServer(appC.serverURI, appC.app, false);
        serverC.getHttpHandler().setAllowEncodedSlash(true);
        serverC.start();

        logger.info("All server has been started...");

        addShutdownHooks(serverA, serverB, serverC);
        Thread.currentThread().join();
    }

    private record AppURLConfig(ResourceConfig app, URI serverURI) {
    }
}