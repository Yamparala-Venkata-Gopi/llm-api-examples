from flask import Flask, request, jsonify
import requests
import os

app = Flask(__name__)


@app.get("/health")
def health():
    return jsonify({"status": "ok"})


@app.post("/openai/chat")
def openai_chat():
    data = request.json or {}
    resp = requests.post(
        "https://api.openai.com/v1/chat/completions",
        headers={
            "Content-Type": "application/json",
            "Authorization": f"Bearer {os.environ['OPENAI_API_KEY']}",
        },
        json={
            "model": data.get("model", "gpt-3.5-turbo"),
            "messages": data.get(
                "messages", [{"role": "user", "content": "Hello!"}]
            ),
        },
    )
    return jsonify(resp.json()), resp.status_code


@app.post("/anthropic/chat")
def anthropic_chat():
    data = request.json or {}
    resp = requests.post(
        "https://api.anthropic.com/v1/complete",
        headers={
            "accept": "application/json",
            "anthropic-version": "2023-06-01",
            "content-type": "application/json",
            "x-api-key": os.environ["ANTHROPIC_API_KEY"],
        },
        json={
            "model": data.get("model", "claude-2.1"),
            "prompt": data.get("prompt", "\n\nHuman: Hello!\n\nAssistant:"),
            "max_tokens_to_sample": data.get("max_tokens_to_sample", 256),
        },
    )
    return jsonify(resp.json()), resp.status_code


@app.post("/cohere/chat")
def cohere_chat():
    data = request.json or {}
    resp = requests.post(
        "https://api.cohere.ai/v1/chat",
        headers={
            "accept": "application/json",
            "content-type": "application/json",
            "Authorization": f"Bearer {os.environ['COHERE_API_KEY']}",
        },
        json={
            "chat_history": data.get("chat_history", []),
            "message": data.get("message", "Hello!"),
            "connectors": data.get("connectors", []),
        },
    )
    return jsonify(resp.json()), resp.status_code


@app.post("/mistral/chat")
def mistral_chat():
    data = request.json or {}
    resp = requests.post(
        "https://api.mistral.ai/v1/chat/completions",
        headers={
            "Content-Type": "application/json",
            "Accept": "application/json",
            "Authorization": f"Bearer {os.environ['MISTRAL_API_KEY']}",
        },
        json={
            "model": data.get("model", "mistral-tiny"),
            "messages": data.get(
                "messages", [{"role": "user", "content": "Hello!"}]
            ),
        },
    )
    return jsonify(resp.json()), resp.status_code


@app.post("/google/chat")
def google_chat():
    data = request.json or {}
    resp = requests.post(
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"
        f"?key={os.environ['GOOGLE_API_KEY']}",
        headers={"Content-Type": "application/json"},
        json={
            "contents": data.get(
                "contents",
                [{"parts": [{"text": "Hello!"}]}],
            )
        },
    )
    return jsonify(resp.json()), resp.status_code


@app.post("/groq/chat")
def groq_chat():
    data = request.json or {}
    resp = requests.post(
        "https://api.groq.com/openai/v1/chat/completions",
        headers={
            "Content-Type": "application/json",
            "Authorization": f"Bearer {os.environ['GROQ_API_KEY']}",
        },
        json={
            "model": data.get("model", "mixtral-8x7b-32768"),
            "messages": data.get(
                "messages",
                [
                    {"role": "system", "content": "You are a helpful assistant."},
                    {"role": "user", "content": "Hello!"},
                ],
            ),
        },
    )
    return jsonify(resp.json()), resp.status_code


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 5000)))
