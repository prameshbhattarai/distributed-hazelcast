package org.distributed;

import org.distributed.application.AppA;
import org.distributed.application.AppB;
import org.distributed.application.AppC;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;


public class Main {
    private static AppURLConfig createApplicationA() {
        var app = new AppA().server();
        var uri = URI.create("http://localhost:8080");
        return new AppURLConfig(app, uri);
    }

    private static AppURLConfig createApplicationB() {
        var app = new AppB().server();
        var uri = URI.create("http://localhost:8081");
        return new AppURLConfig(app, uri);
    }

    private static AppURLConfig createApplicationC() {
        var app = new AppC().server();
        var uri = URI.create("http://localhost:8082");
        return new AppURLConfig(app, uri);
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

        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        serverC::shutdownNow,
                        "serverShutdown"
                )
        );

        Thread.currentThread().join();
    }

    private record AppURLConfig(ResourceConfig app, URI serverURI) {
    }
}