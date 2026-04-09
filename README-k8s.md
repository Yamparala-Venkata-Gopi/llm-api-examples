# Kubernetes Deployment Guide

This guide explains how to deploy the LLM API application on a Kubernetes cluster using the included Helm chart.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Docker Images](#docker-images)
- [Deploying with Helm](#deploying-with-helm)
- [Configuring API Keys](#configuring-api-keys)
- [Choosing the App (Python or Java)](#choosing-the-app-python-or-java)
- [Python and Java in the Same Namespace](#python-and-java-in-the-same-namespace)
- [Exposing the Service](#exposing-the-service)
- [Verifying the Deployment](#verifying-the-deployment)
- [Upgrading and Uninstalling](#upgrading-and-uninstalling)

---

## Prerequisites

Make sure the following tools are installed and configured before you begin:

| Tool | Version | Notes |
|------|---------|-------|
| [kubectl](https://kubernetes.io/docs/tasks/tools/) | v1.24+ | Connected to your cluster (`kubectl cluster-info`) |
| [Helm](https://helm.sh/docs/intro/install/) | v3.x | `helm version` |
| [Docker](https://docs.docker.com/get-docker/) | 20.x+ | Needed only if you build images locally |
| A running Kubernetes cluster | — | EKS, GKE, AKS, k3s, minikube, etc. |

---

## Docker Images

The CI/CD pipeline automatically builds and pushes images to Docker Hub on every push to `main` that touches `python-app/` or `java-app/`.

### Pre-built images (Docker Hub)

```
<DOCKERHUB_USERNAME>/llm-api-python:latest
<DOCKERHUB_USERNAME>/llm-api-java:latest
```

Replace `<DOCKERHUB_USERNAME>` with the actual Docker Hub username configured in the repository secrets.

### Building images locally

If you want to build the images yourself:

```bash
# Python app
docker build -t my-org/llm-api-python:latest ./python-app

# Java app
docker build -t my-org/llm-api-java:latest ./java-app
```

Push them to your registry:

```bash
docker push my-org/llm-api-python:latest
docker push my-org/llm-api-java:latest
```

---

## Deploying with Helm

The Helm chart lives in `helm/llm-api-app/` (folder contains `Chart.yaml`).

### Working directory and chart path

Helm resolves the chart path **relative to your current shell directory**. If you see `path "./helm/llm-api-app" not found`, you are not in the directory you think you are.

| Your current directory | Use this chart argument |
|-------------------------|-------------------------|
| Repository root (where `helm/` and `python-app/` sit) | `./helm/llm-api-app` |
| Inside `helm/` (sibling of `llm-api-app`) | `./llm-api-app` |
| Inside `helm/llm-api-app/` (next to `Chart.yaml`) | `.` |

**Convention:** The `helm install`, `helm upgrade`, and `helm uninstall` examples below assume you **`cd` to the repository root** first. If you stay in `helm/`, replace `./helm/llm-api-app` with `./llm-api-app` in every command.

### 1. Install with the Python app (default)

From the repository root:

```bash
helm upgrade --install llm-api ./helm/llm-api-app \
  --set app=python \
  --set python.image.repository=<DOCKERHUB_USERNAME>/llm-api-python \
  --set python.image.tag=latest \
  --set apiKeys.openai="<YOUR_OPENAI_API_KEY>" \
  --set apiKeys.anthropic="<YOUR_ANTHROPIC_API_KEY>" \
  --set apiKeys.cohere="<YOUR_COHERE_API_KEY>" \
  --set apiKeys.mistral="<YOUR_MISTRAL_API_KEY>" \
  --set apiKeys.google="<YOUR_GOOGLE_API_KEY>" \
  --set apiKeys.groq="<YOUR_GROQ_API_KEY>"
```

Add **`-n <namespace> --create-namespace`** on the same command when you are not using `default`.

### 2. Install with the Java app

From the repository root:

```bash
helm upgrade --install llm-api ./helm/llm-api-app \
  --set app=java \
  --set java.image.repository=<DOCKERHUB_USERNAME>/llm-api-java \
  --set java.image.tag=latest \
  --set apiKeys.openai="<YOUR_OPENAI_API_KEY>" \
  --set apiKeys.anthropic="<YOUR_ANTHROPIC_API_KEY>" \
  --set apiKeys.cohere="<YOUR_COHERE_API_KEY>" \
  --set apiKeys.mistral="<YOUR_MISTRAL_API_KEY>" \
  --set apiKeys.google="<YOUR_GOOGLE_API_KEY>" \
  --set apiKeys.groq="<YOUR_GROQ_API_KEY>"
```

Add **`-n <namespace> --create-namespace`** when deploying outside `default`.

---

## Configuring API Keys

### Option A — Helm `--set` flags (quick start)

Pass keys directly on the command line as shown above. Helm will create a Kubernetes `Secret` from the provided values.

### Option B — Custom `values.yaml` (recommended for teams)

Create a local overrides file (do **not** commit this file):

```yaml
# my-values.yaml
app: python

python:
  image:
    repository: <DOCKERHUB_USERNAME>/llm-api-python
    tag: latest

apiKeys:
  openai: "sk-..."
  anthropic: "sk-ant-..."
  cohere: "..."
  mistral: "..."
  google: "..."
  groq: "..."
```

Then install or upgrade using (from repository root; one command covers both):

```bash
helm upgrade --install llm-api ./helm/llm-api-app -f my-values.yaml
```

Add **`-n <namespace> --create-namespace`** when needed.

### Option C — External secret management

Leave all `apiKeys` fields empty in `values.yaml` and inject the secret from an external source (e.g., AWS Secrets Manager, Vault, Sealed Secrets). The expected Kubernetes `Secret` must have the following keys:

```
OPENAI_API_KEY
ANTHROPIC_API_KEY
COHERE_API_KEY
MISTRAL_API_KEY
GOOGLE_API_KEY
GROQ_API_KEY
```

Create it manually (verify the exact secret name first with `kubectl get secrets`; the default Helm-generated name is `<release-name>-llm-api-app-api-keys`):

```bash
kubectl create secret generic llm-api-llm-api-app-api-keys \
  --from-literal=OPENAI_API_KEY="sk-..." \
  --from-literal=ANTHROPIC_API_KEY="sk-ant-..." \
  --from-literal=COHERE_API_KEY="..." \
  --from-literal=MISTRAL_API_KEY="..." \
  --from-literal=GOOGLE_API_KEY="..." \
  --from-literal=GROQ_API_KEY="..."
```

Then install the chart with empty apiKeys so Helm does not overwrite the secret (from repository root):

```bash
helm upgrade --install llm-api ./helm/llm-api-app --set app=python
```

---

## Choosing the App (Python or Java)

The chart deploys either the Python app (port 5000) or the Java app (port 8080) based on the `app` value.

| Value | Image | Port |
|-------|-------|------|
| `python` (default) | `python.image.repository:tag` | 5000 |
| `java` | `java.image.repository:tag` | 8080 |

Switch at any time with **`helm upgrade --install`** (from repository root). The Deployment **selector** stays the same so Kubernetes accepts the change:

```bash
helm upgrade --install llm-api ./helm/llm-api-app \
  --set app=java \
  --set java.image.repository=<DOCKERHUB_USERNAME>/llm-api-java \
  --set java.image.tag=latest
```

**If you still see `spec.selector ... field is immutable`:** you deployed an older chart (before 0.2.0) that put `app: python|java` into the selector. Kubernetes never allows changing that. Uninstall once, then install again with the current chart:

```bash
helm uninstall llm-api
# add -n <namespace> if you did not use default
helm upgrade --install llm-api ./helm/llm-api-app --set app=java ...
```

### Python and Java in the same namespace

The chart deploys **one** runtime per Helm release (`app` is either `python` or `java`). To run **both** at once, install **two releases** in the same namespace.

Without extra settings, both releases would use the same Kubernetes object names (`llm-api-app`), which collides. Set **`fullnameOverride`** on each release so Deployment, Service, and Secret names differ:

```bash
# Python — release name llm-api-python, stable resource prefix
helm upgrade --install llm-api-python ./helm/llm-api-app \
  --set app=python \
  --set fullnameOverride=llm-api-python \
  --set python.image.repository=<DOCKERHUB_USERNAME>/llm-api-python \
  --set python.image.tag=latest

# Java — release name llm-api-java
helm upgrade --install llm-api-java ./helm/llm-api-app \
  --set app=java \
  --set fullnameOverride=llm-api-java \
  --set java.image.repository=<DOCKERHUB_USERNAME>/llm-api-java \
  --set java.image.tag=latest
```

Each release keeps a distinct **`app.kubernetes.io/instance`** label (the release name), so selectors stay disjoint. You get two Services (for example `llm-api-python` and `llm-api-java` on ports 5000 and 8080), two Deployments, and two API-key Secrets—pass the same `apiKeys` values to both if you want identical provider credentials.

Add **`-n <namespace> --create-namespace`** to both commands when not using `default`. Uninstall each release separately, for example `helm uninstall llm-api-python` and `helm uninstall llm-api-java`.

---

## Exposing the Service

The chart creates a `ClusterIP` service by default. Choose the exposure method that fits your cluster:

### NodePort (local clusters, minikube)

From the repository root:

```bash
helm upgrade llm-api ./helm/llm-api-app --set service.type=NodePort
# minikube only
minikube service llm-api-llm-api-app
```

### LoadBalancer (cloud clusters — EKS, GKE, AKS)

From the repository root:

```bash
helm upgrade llm-api ./helm/llm-api-app --set service.type=LoadBalancer
kubectl get svc llm-api-llm-api-app   # wait for EXTERNAL-IP
```

### Ingress (production — requires an Ingress controller such as nginx or Traefik)

Apply a standard Ingress manifest pointing to the ClusterIP service:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: llm-api-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
    - host: llm-api.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: llm-api-llm-api-app
                port:
                  number: 5000   # use 8080 for Java
```

```bash
kubectl apply -f ingress.yaml
```

---

## Verifying the Deployment

Check that the pod is running:

```bash
kubectl get pods -l app.kubernetes.io/instance=llm-api
kubectl logs -l app.kubernetes.io/instance=llm-api
```

Hit the health endpoint with a port-forward:

```bash
kubectl port-forward svc/llm-api-llm-api-app 8080:5000   # python
# or
kubectl port-forward svc/llm-api-llm-api-app 8080:8080   # java

curl http://localhost:8080/health
# Expected: {"status": "ok"}
```

Test an LLM call:

```bash
curl -X POST http://localhost:8080/openai/chat \
  -H "Content-Type: application/json" \
  -d '{"messages": [{"role": "user", "content": "Hello!"}]}'
```

---

## Upgrading and Uninstalling

### Upgrade to a new image tag

From the repository root:

```bash
helm upgrade llm-api ./helm/llm-api-app \
  --set python.image.tag=<NEW_TAG>
```

### Scale replicas

From the repository root:

```bash
helm upgrade llm-api ./helm/llm-api-app --set replicaCount=3
```

### Uninstall

```bash
helm uninstall llm-api
# if you installed into a non-default namespace:
# helm uninstall llm-api -n test
```

> **Note:** The `Secret` containing the API keys is deleted along with the release. Back up your keys before uninstalling if needed.
