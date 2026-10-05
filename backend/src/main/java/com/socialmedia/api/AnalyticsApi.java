package com.socialmedia.api;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AnalyticsApi {

    public static void handle(HttpExchange exchange)
            throws IOException {

        String outputPath =
                "/socialmedia/output/part-r-00000";

        String response;

        try {

            Process process = new ProcessBuilder(
                    "docker",
                    "exec",
                    "socialmedia-namenode",
                    "hdfs",
                    "dfs",
                    "-cat",
                    outputPath
            ).redirectErrorStream(true).start();

            String result = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            process.waitFor();

            response = convertToJson(result);

        } catch (Exception e) {

            response =
                    "{\"status\":\"error\",\"message\":\""
                    + e.getMessage().replace("\"", "'")
                    + "\"}";
        }

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        exchange.sendResponseHeaders(
                200,
                response.getBytes(StandardCharsets.UTF_8).length
        );

        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );
        }
    }

    private static String convertToJson(String result) {

        String[] lines = result.trim().split("\\r?\\n");

        StringBuilder json = new StringBuilder();

        json.append("{\"status\":\"success\",\"platforms\":[");

        boolean first = true;

        for (String line : lines) {

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.trim().split("\\s+", 2);

            if (parts.length < 2) {
                continue;
            }

            String platform = parts[0];
            String data = parts[1];

            int likes = extractValue(data, "Likes=");
            int comments = extractValue(data, "Comments=");
            int shares = extractValue(data, "Shares=");
            int engagement =
                    extractValue(data, "TotalEngagement=");

            if (!first) {
                json.append(",");
            }

            json.append("{");
            json.append("\"platform\":\"")
                    .append(platform)
                    .append("\",");
            json.append("\"likes\":")
                    .append(likes)
                    .append(",");
            json.append("\"comments\":")
                    .append(comments)
                    .append(",");
            json.append("\"shares\":")
                    .append(shares)
                    .append(",");
            json.append("\"totalEngagement\":")
                    .append(engagement);
            json.append("}");

            first = false;
        }

        json.append("]}");

        return json.toString();
    }

    private static int extractValue(
            String data,
            String key) {

        int start = data.indexOf(key);

        if (start == -1) {
            return 0;
        }

        start += key.length();

        int end = data.indexOf(",", start);

        if (end == -1) {
            end = data.length();
        }

        try {
            return Integer.parseInt(
                    data.substring(start, end).trim()
            );
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}