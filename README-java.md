# LLM API call examples

This repository contains a list of working code examples for calling various LLM APIs.

[README.md](README.md) is the source of truth and contains all examples in `curl` format.

[README-python.md](README-python.md) contains the same examples in Python.

[README-java.md](README-java.md) contains the same examples in Java using `java.net.http.HttpClient` (Java 11+).

See also: [List of cloud hosts for inference and fine-tuning](https://github.com/jamesmurdza/awesome-inference-hosts)

## Table of Contents

- [OpenAI](#openai)
- [Anthropic](#anthropic)
- [Cohere](#cohere)
- [Mistral](#mistral)
- [Google](#google)
- [Groq](#groq)

## OpenAI

🔑 Get API key [here](https://platform.openai.com/account/api-keys).

📃 API [docs](https://platform.openai.com/docs/).

### Chat
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "model": "gpt-3.5-turbo",
      "messages": [
        {"role": "system", "content": "You are a helpful assistant."},
        {"role": "user", "content": "Hello!"}
      ]
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

### Embeddings
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "input": "The food was delicious and the wine...",
      "model": "text-embedding-ada-002",
      "encoding_format": "float"
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.openai.com/v1/embeddings"))
    .header("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

## Anthropic

🔑 Get API key [here](https://console.anthropic.com/account/keys).

📃 API [docs](https://docs.anthropic.com/).

### Chat
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "model": "claude-2.1",
      "prompt": "\\n\\nHuman: Hello, world!\\n\\nAssistant:",
      "max_tokens_to_sample": 256
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.anthropic.com/v1/complete"))
    .header("accept", "application/json")
    .header("anthropic-version", "2023-06-01")
    .header("content-type", "application/json")
    .header("x-api-key", System.getenv("ANTHROPIC_API_KEY"))
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

## Cohere

🔑 Get API key [here](https://dashboard.cohere.com/api-keys).

📃 API [docs](https://docs.cohere.com/).

### Chat
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "chat_history": [
        {"role": "USER", "message": "Who discovered gravity?"},
        {"role": "CHATBOT", "message": "The man who is widely credited with discovering gravity is Sir Isaac Newton"}
      ],
      "message": "What year was he born?",
      "connectors": [{"id": "web-search"}]
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.cohere.ai/v1/chat"))
    .header("accept", "application/json")
    .header("content-type", "application/json")
    .header("Authorization", "Bearer " + System.getenv("COHERE_API_KEY"))
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

### Embeddings
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "texts": ["hello", "goodbye"],
      "truncate": "END"
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.cohere.ai/v1/embed"))
    .header("accept", "application/json")
    .header("content-type", "application/json")
    .header("Authorization", "Bearer " + System.getenv("COHERE_API_KEY"))
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

## Mistral

🔑 Get API key [here](https://console.mistral.ai/users/api-keys/).

📃 API [docs](https://docs.mistral.ai/api/).

### Chat
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "model": "mistral-tiny",
      "messages": [{"role": "user", "content": "Who is the most renowned French writer?"}]
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.mistral.ai/v1/chat/completions"))
    .header("Content-Type", "application/json")
    .header("Accept", "application/json")
    .header("Authorization", "Bearer " + System.getenv("MISTRAL_API_KEY"))
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

### Embeddings
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "model": "mistral-embed",
      "input": ["Embed this sentence.", "As well as this one."]
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.mistral.ai/v1/embeddings"))
    .header("Content-Type", "application/json")
    .header("Accept", "application/json")
    .header("Authorization", "Bearer " + System.getenv("MISTRAL_API_KEY"))
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

## Google

🔑 Get API key [here](https://makersuite.google.com/app/apikey).

📃 API [docs](https://ai.google.dev/api/rest).

### Chat
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "contents": [
        {
          "parts": [{"text": "Write a story about a magic backpack."}]
        }
      ]
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + System.getenv("GOOGLE_API_KEY")))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

### Embeddings
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "contents": [
        {
          "parts": [{"text": "This is a sentence."}]
        }
      ]
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/embedding-001:generateContent?key=" + System.getenv("GOOGLE_API_KEY")))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```

## Groq

🔑 Get API key [here](https://console.groq.com/keys).

📃 API [docs](https://console.groq.com/docs/).

### Chat
```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

HttpClient client = HttpClient.newHttpClient();
String body = """
    {
      "model": "mixtral-8x7b-32768",
      "messages": [
        {"role": "system", "content": "You are a helpful assistant."},
        {"role": "user", "content": "Hello!"}
      ]
    }""";
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer " + System.getenv("GROQ_API_KEY"))
    .POST(HttpRequest.BodyPublishers.ofString(body))
    .build();
HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
```
