package com.example.llm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private ResponseEntity<Map<String, Object>> post(
            String url,
            Map<String, String> headers,
            Map<String, Object> body) {
        try {
            String jsonBody = mapper.writeValueAsString(body);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
            headers.forEach(builder::header);

            HttpResponse<String> response =
                    httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = mapper.readValue(response.body(), Map.class);
            return ResponseEntity.status(response.statusCode()).body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to call LLM API. Check server logs for details."));
        }
    }

    public ResponseEntity<Map<String, Object>> openaiChat(Map<String, Object> input) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        Map<String, Object> body = Map.of(
                "model", input.getOrDefault("model", "gpt-3.5-turbo"),
                "messages", input.getOrDefault("messages",
                        List.of(Map.of("role", "user", "content", "Hello!")))
        );
        return post(
                "https://api.openai.com/v1/chat/completions",
                Map.of("Content-Type", "application/json",
                       "Authorization", "Bearer " + apiKey),
                body);
    }

    public ResponseEntity<Map<String, Object>> anthropicChat(Map<String, Object> input) {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        Map<String, Object> body = Map.of(
                "model", input.getOrDefault("model", "claude-2.1"),
                "prompt", input.getOrDefault("prompt", "\n\nHuman: Hello!\n\nAssistant:"),
                "max_tokens_to_sample", input.getOrDefault("max_tokens_to_sample", 256)
        );
        return post(
                "https://api.anthropic.com/v1/complete",
                Map.of("accept", "application/json",
                       "anthropic-version", "2023-06-01",
                       "content-type", "application/json",
                       "x-api-key", apiKey),
                body);
    }

    public ResponseEntity<Map<String, Object>> cohereChat(Map<String, Object> input) {
        String apiKey = System.getenv("COHERE_API_KEY");
        Map<String, Object> body = Map.of(
                "message", input.getOrDefault("message", "Hello!"),
                "chat_history", input.getOrDefault("chat_history", List.of()),
                "connectors", input.getOrDefault("connectors", List.of())
        );
        return post(
                "https://api.cohere.ai/v1/chat",
                Map.of("accept", "application/json",
                       "content-type", "application/json",
                       "Authorization", "Bearer " + apiKey),
                body);
    }

    public ResponseEntity<Map<String, Object>> mistralChat(Map<String, Object> input) {
        String apiKey = System.getenv("MISTRAL_API_KEY");
        Map<String, Object> body = Map.of(
                "model", input.getOrDefault("model", "mistral-tiny"),
                "messages", input.getOrDefault("messages",
                        List.of(Map.of("role", "user", "content", "Hello!")))
        );
        return post(
                "https://api.mistral.ai/v1/chat/completions",
                Map.of("Content-Type", "application/json",
                       "Accept", "application/json",
                       "Authorization", "Bearer " + apiKey),
                body);
    }

    public ResponseEntity<Map<String, Object>> googleChat(Map<String, Object> input) {
        String apiKey = System.getenv("GOOGLE_API_KEY");
        Map<String, Object> body = Map.of(
                "contents", input.getOrDefault("contents",
                        List.of(Map.of("parts", List.of(Map.of("text", "Hello!")))))
        );
        return post(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey,
                Map.of("Content-Type", "application/json"),
                body);
    }

    public ResponseEntity<Map<String, Object>> groqChat(Map<String, Object> input) {
        String apiKey = System.getenv("GROQ_API_KEY");
        Map<String, Object> body = Map.of(
                "model", input.getOrDefault("model", "mixtral-8x7b-32768"),
                "messages", input.getOrDefault("messages",
                        List.of(
                                Map.of("role", "system", "content", "You are a helpful assistant."),
                                Map.of("role", "user", "content", "Hello!")
                        ))
        );
        return post(
                "https://api.groq.com/openai/v1/chat/completions",
                Map.of("Content-Type", "application/json",
                       "Authorization", "Bearer " + apiKey),
                body);
    }
}
