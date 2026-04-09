package com.example.llm.controller;

import com.example.llm.service.LlmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LlmController {

    private final LlmService llmService;

    public LlmController(LlmService llmService) {
        this.llmService = llmService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping("/openai/chat")
    public ResponseEntity<Map<String, Object>> openaiChat(
            @RequestBody(required = false) Map<String, Object> body) {
        return llmService.openaiChat(body != null ? body : Map.of());
    }

    @PostMapping("/anthropic/chat")
    public ResponseEntity<Map<String, Object>> anthropicChat(
            @RequestBody(required = false) Map<String, Object> body) {
        return llmService.anthropicChat(body != null ? body : Map.of());
    }

    @PostMapping("/cohere/chat")
    public ResponseEntity<Map<String, Object>> cohereChat(
            @RequestBody(required = false) Map<String, Object> body) {
        return llmService.cohereChat(body != null ? body : Map.of());
    }

    @PostMapping("/mistral/chat")
    public ResponseEntity<Map<String, Object>> mistralChat(
            @RequestBody(required = false) Map<String, Object> body) {
        return llmService.mistralChat(body != null ? body : Map.of());
    }

    @PostMapping("/google/chat")
    public ResponseEntity<Map<String, Object>> googleChat(
            @RequestBody(required = false) Map<String, Object> body) {
        return llmService.googleChat(body != null ? body : Map.of());
    }

    @PostMapping("/groq/chat")
    public ResponseEntity<Map<String, Object>> groqChat(
            @RequestBody(required = false) Map<String, Object> body) {
        return llmService.groqChat(body != null ? body : Map.of());
    }
}
