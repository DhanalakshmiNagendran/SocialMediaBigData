package com.socialmedia.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class BackendServer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

       server.createContext("/api/status", BackendServer::status);
       server.createContext("/api/analytics", AnalyticsApi::handle);
       server.createContext("/", BackendServer::serveFrontend);

        server.start();

        System.out.println("=================================");
        System.out.println("Social Media Big Data Backend");
        System.out.println("Server started at http://localhost:8080");
        System.out.println("=================================");
    }
    private static void serveFrontend(HttpExchange exchange)
        throws IOException {

    String requestPath = exchange.getRequestURI().getPath();

    if (requestPath.equals("/")) {
        requestPath = "/index.html";
    }

    java.nio.file.Path filePath =
            java.nio.file.Paths.get(
                    "..",
                    "frontend",
                    requestPath.substring(1)
            ).normalize().toAbsolutePath();

    if (!java.nio.file.Files.exists(filePath)
            || java.nio.file.Files.isDirectory(filePath)) {

        String response = "404 - File Not Found";

        exchange.sendResponseHeaders(
                404,
                response.getBytes(StandardCharsets.UTF_8).length
        );

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );
        }

        return;
    }

    String contentType = "text/plain";

    if (requestPath.endsWith(".html")) {
        contentType = "text/html";
    } else if (requestPath.endsWith(".css")) {
        contentType = "text/css";
    } else if (requestPath.endsWith(".js")) {
        contentType = "application/javascript";
    }

    byte[] fileBytes =
            java.nio.file.Files.readAllBytes(filePath);

    exchange.getResponseHeaders().set(
            "Content-Type",
            contentType
    );

    exchange.sendResponseHeaders(
            200,
            fileBytes.length
    );

    try (OutputStream output = exchange.getResponseBody()) {
        output.write(fileBytes);
    }
}

    private static void status(HttpExchange exchange)
            throws IOException {

        String response =
                "{\"status\":\"success\",\"message\":\"Backend API is running\"}";

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json");

        exchange.sendResponseHeaders(
                200,
                response.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(
                    response.getBytes(StandardCharsets.UTF_8));
        }
    }
}