package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
// The java.util.Arrays import is not used and can be removed, but kept for minimal change.
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String LOGIN_URL = "https://tb.piltismart.com/api/auth/login";
    private static final String LOGIN_PAYLOAD = """
        {
            "username": "poojashri@gmail.com",
            "password": "040897"
        }
    """;


    public static void main(String[] args) {

        String token = retrieveToken();

        if (token != null) {
            System.out.println(token);
        } else {
            System.err.println("Failed to retrieve token.");
        }
    }
    public static String retrieveToken() {

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(LOGIN_PAYLOAD))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) { // Logic fix: Added 201
                return extractToken(response.body());
            } else {
                System.err.println(  response.statusCode());
                System.err.println(  response.body());
                return null;
            }

        } catch (IOException e) {
            System.err.println( e.getMessage());
            return null;
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            System.err.println(e.getMessage());
            return null;
        }
    }/


    private static String extractToken(String jsonResponse) throws IOException {
        //
        JsonNode rootNode = MAPPER.readTree(jsonResponse);
        JsonNode dataNode = rootNode.get("data");
        if (dataNode != null) {
            JsonNode tokenNode = dataNode.get("token");
            if (tokenNode != null && tokenNode.isTextual()) {
                return tokenNode.asText();
            }
        }

        return jsonResponse;
    }
}
