# 🚀 Lovable — AI-Powered React App Builder

> A full-stack, production-grade backend clone of [Lovable.dev](https://lovable.dev) — chat with an AI to generate, edit, preview, and deploy React applications in real time.

| | |
|---|---|
| **Backend (this repo)** | Spring Boot monolith — AI generation, file storage, K8s deployments, billing |
| **Frontend** | [shashi162003/project-companion](https://github.com/shashi162003/project-companion) — React + TypeScript client |
| **Walkthrough Video** | [https://drive.google.com/file/d/1uNTxHjOb0h0J0qZrkhoLZaVnu6IrYid-/view?usp=sharing](https://drive.google.com/file/d/1uNTxHjOb0h0J0qZrkhoLZaVnu6IrYid-/view?usp=sharing)

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| ![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk) | Core language |
| ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot) | Web framework, JPA, Security, Validation |
| ![Spring AI](https://img.shields.io/badge/Spring%20AI-1.x-brightgreen?logo=spring) | ChatClient, streaming, tool-calling, advisors |
| ![OpenAI](https://img.shields.io/badge/OpenAI-GPT-412991?logo=openai) | LLM backend (configurable; Ollama also supported) |
| ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-4169E1?logo=postgresql) | Primary relational database |
| ![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis) | Preview routing table + usage cache |
| ![MinIO](https://img.shields.io/badge/MinIO-Object%20Storage-C72E49?logo=minio) | Project file storage (S3-compatible) |
| ![Kafka](https://img.shields.io/badge/Apache%20Kafka-7.5-231F20?logo=apachekafka) | Event streaming (Confluent local) |
| ![Kubernetes](https://img.shields.io/badge/Kubernetes-Fabric8-326CE5?logo=kubernetes) | Runner pod pool management |
| ![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker) | Local infrastructure |
| ![Stripe](https://img.shields.io/badge/Stripe-Payments-635BFF?logo=stripe) | Subscription billing & webhooks |
| ![Node.js](https://img.shields.io/badge/Node.js-Reverse%20Proxy-339933?logo=nodedotjs) | Redis-backed wildcard HTTP/WS proxy |
| ![MapStruct](https://img.shields.io/badge/MapStruct-DTO%20Mapping-red) | Compile-time DTO ↔ Entity mapping |
| ![Lombok](https://img.shields.io/badge/Lombok-Boilerplate%20Reduction-pink) | Annotations for cleaner code |
| ![Springdoc](https://img.shields.io/badge/SpringDoc-OpenAPI%20UI-85EA2D?logo=swagger) | Auto-generated Swagger docs |

---

## 📐 Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client (React Frontend)                  │
└───────────────┬─────────────────────────────────┬───────────────┘
                │ REST / SSE                       │ Preview URL
                ▼                                  ▼
┌───────────────────────────┐     ┌────────────────────────────────┐
│  Spring Boot Monolith     │     │  Node.js Wildcard Proxy        │
│  (Lovable Backend)        │     │  project-{id}.app.domain.com   │
│                           │     │  ──────────────────────────    │
│  ┌─────────────────────┐  │     │  Reads Redis route table       │
│  │  Spring AI          │  │     │  Forwards HTTP + WebSocket     │
│  │  ChatClient (SSE)   │  │     │  to pod IP:5173                │
│  │  + FileTree Advisor │  │     └──────────────┬─────────────────┘
│  │  + CodeGen Tools    │  │                    │
│  └────────┬────────────┘  │                    ▼
│           │               │     ┌────────────────────────────────┐
│           ▼               │     │  Kubernetes Namespace          │
│  ┌─────────────────────┐  │     │  lovable-app                   │
│  │  LlmResponseParser  │  │     │                                │
│  │  Extracts <file>    │  │     │  ┌──────────────────────────┐  │
│  │  tags → MinIO       │  │ ──► │  │  Runner Pod (idle/busy)  │  │
│  └─────────────────────┘  │     │  │  ┌──────────┐┌────────┐ │  │
│                           │     │  │  │  runner  ││syncer  │ │  │
│  Auth / Projects /        │     │  │  │  node:20 ││minio/mc│ │  │
│  Members / Billing /      │     │  │  │  Vite:   ││MinIO   │ │  │
│  Usage Controllers        │     │  │  │  5173    ││Mirror  │ │  │
└───────────────────────────┘     │  └──────────────────────────┘  │
         │   │   │                └────────────────────────────────┘
         │   │   │
   ┌─────┘   │   └──────┐
   ▼         ▼          ▼
PostgreSQL  Redis      MinIO
(pgvector)  (routes +  (project
            cache)      files)
```

---

## 🗂 Project Structure

```
lovable/
├── src/main/java/com/devshashi/lovable/
│   ├── config/           # Spring beans: AI, CORS, Redis, MinIO, K8s, Stripe
│   ├── controller/       # REST endpoints: Auth, Chat, Project, File, Billing, Usage
│   ├── dto/              # Request/Response DTOs (records)
│   ├── entity/           # JPA entities: User, Project, ChatSession, ChatMessage,
│   │                     #   ChatEvent, ProjectFile, Preview, Subscription, Plan, UsageLog
│   ├── enums/            # ChatEventType, MessageRole, ProjectRole, PreviewStatus, etc.
│   ├── error/            # GlobalExceptionHandler, custom exceptions
│   ├── llm/
│   │   ├── advisors/     # FileTreeContextAdvisor — injects live file tree into prompts
│   │   ├── tools/        # CodeGenerationTools — @Tool annotated read_files function
│   │   ├── LlmResponseParser.java  # Parses XML tags from LLM output
│   │   └── PromptUtils.java        # System prompt constants
│   ├── mapper/           # MapStruct mappers for all entities
│   ├── repository/       # Spring Data JPA repositories
│   ├── security/         # JWT filter, WebSecurityConfig, @PreAuthorize expressions
│   └── service/
│       ├── impl/         # Business logic implementations
│       └── *.java        # Service interfaces
├── proxy/                # Node.js reverse proxy (Redis-backed, HTTP + WS)
│   ├── index.js
│   └── Dockerfile
├── k8s/
│   ├── infra.yml         # Namespace, MinIO ExternalName service, Redis, Proxy
│   ├── runner-pods.yml   # Runner pod pool (node:20 + minio/mc sidecar)
│   ├── lovable-proxy.yml # Node.js proxy deployment
│   └── policy.yml        # RBAC / NetworkPolicy
├── services.docker-compose.yml   # Local infra: pgvector, MinIO, Kafka
└── pom.xml
```

---

## ⚙️ Prerequisites

- **Java 21+**
- **Maven 3.9+**
- **Docker + Docker Compose**
- **Node.js 20+** (for the proxy)
- **kubectl** + **[kind](https://kind.sigs.k8s.io/)** — recommended for local K8s (see note below); only needed for preview deployments
- An **OpenAI API key** (or a locally running Ollama instance)
- A **Stripe account** (test keys are fine) + **[Stripe CLI](https://stripe.com/docs/stripe-cli)** for local webhook forwarding

> **Why kind over Minikube?** kind (Kubernetes IN Docker) runs cluster nodes as Docker containers, so it shares the same Docker daemon as your other services. This means runner pods can resolve `host.docker.internal` to reach your MinIO instance on the host machine without any extra networking config — which is exactly what `infra.yml` relies on. Minikube runs in its own VM/network and makes this significantly harder to set up.

---

## 🏃 Running Locally

### 1. Start Infrastructure (Docker Compose)

```bash
docker compose -f services.docker-compose.yml up -d
```

This starts:

| Container | Port | Purpose |
|---|---|---|
| `pgvector-db` | `9010` | PostgreSQL with pgvector extension |
| `minio` | `9000` (API) / `9001` (Console) | Object storage for project files |
| `kafka-lovable-me` | `29092` (host) | Event streaming |

> Visit **http://localhost:9001** to access the MinIO console (`minioadmin` / `minioadmin123`).

**You must create two buckets and upload the starter template before running the app:**

**Step A — Create the buckets**

Create these two buckets in the MinIO console (or via the `mc` CLI):

| Bucket name | Purpose |
|---|---|
| `projects` | Stores all live project files, keyed as `{projectId}/path/to/file` |
| `starter-projects` | Holds the read-only project templates that new projects are bootstrapped from |

```bash
# Using the MinIO CLI (mc):
mc alias set local http://localhost:9000 minioadmin minioadmin123
mc mb local/projects
mc mb local/starter-projects
```

**Step B — Upload the starter template**

When a new project is created, `ProjectTemplateServiceImpl` copies all files from:

```
starter-projects/react-vite-tailwind-daisyui-starter/
```

into:

```
projects/{newProjectId}/
```

You need to upload the template folder (included in the root of this repo as `react-vite-tailwind-daisyui-starter/`) to MinIO **exactly** under that path:

```bash
# Upload the entire template folder preserving structure
mc cp --recursive react-vite-tailwind-daisyui-starter/ \
  local/starter-projects/react-vite-tailwind-daisyui-starter/
```

Or drag-and-drop the folder via the MinIO console at **http://localhost:9001** into the `starter-projects` bucket, making sure the top-level folder is named `react-vite-tailwind-daisyui-starter`.

> If this step is skipped, creating a new project will throw a `RuntimeException: Failed to initialize project from template`.

### 2. Configure `application.yaml`

Edit `src/main/resources/application.yaml` and fill in your secrets:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:9010/pgvector-test
    username: user
    password: password

  ai:
    openai:
      api-key: sk-...           # Your OpenAI key
      chat.options:
        model: gpt-4o-mini      # or gpt-4o, gpt-4-turbo, etc.
        temperature: 0.0

jwt:
  secret-key: <your-256-bit-secret>

stripe:
  api.secret: sk_test_...       # Stripe test secret key
  webhook.secret: whsec_...     # Stripe webhook signing secret

minio:
  url: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin123
  project-bucket: projects
```

> **Tip:** You can also switch to **Ollama** for fully local inference. Set `spring.ai.ollama.chat.options.model: qwen3:4b` and update `AiConfig.java` to use `OllamaChatModel` instead.

### 4. Set Up Stripe Webhook Forwarding (Required for Billing)

Stripe sends webhook events (subscription created, updated, cancelled) to your `/api/billing/webhook` endpoint. In local development, Stripe can't reach `localhost` directly — you need the **Stripe CLI** to forward events.

```bash
# Install Stripe CLI (macOS)
brew install stripe/stripe-cli/stripe

# Login
stripe login

# Forward webhooks to your local server
stripe listen --forward-to http://localhost:8080/api/billing/webhook
```

The CLI will print a **webhook signing secret** that looks like `whsec_...`. Copy it into your `application.yaml`:

```yaml
stripe:
  webhook:
    secret: whsec_<the-secret-printed-by-the-cli>
```

> Keep the `stripe listen` process running in a separate terminal whenever you're testing payments. Without it, subscription status will never update after checkout.

To trigger test events manually:
```bash
stripe trigger checkout.session.completed
stripe trigger customer.subscription.updated
stripe trigger customer.subscription.deleted
```

### 5. Run the Spring Boot Application

```bash
./mvnw spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw clean package -DskipTests
java -jar target/lovable-*.jar
```

The API will be available at **http://localhost:8080**.

Swagger UI: **http://localhost:8080/swagger-ui.html**

### 6. (Optional) Run the Node.js Reverse Proxy Locally

```bash
cd proxy
npm install
REDIS_URL=redis://localhost:6379 node index.js
```

The proxy listens on **port 80** and forwards requests based on the `Host` header.

---

## 🔄 Core Flow Walkthroughs

### 🧠 Chat & Code Generation Flow

This is the heart of Lovable. When a user types a prompt like _"Add a dark mode toggle to the header"_:

```
1. POST /api/chat/stream
   Body: { "message": "Add a dark mode toggle", "projectId": 42 }
   Produces: text/event-stream (SSE)

2. ChatController → AiGenerationServiceImpl.streamResponse()

3. ChatSession is created (or reused) for (userId, projectId)

4. ChatClient.prompt() is assembled with:
   ├── System Prompt (PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
   │     → "You are an elite React architect. Stack: React 18 + TypeScript + Vite + Tailwind..."
   │     → Strict interaction protocol: Analyze → Plan → Execute → Stop
   │     → XML output format: <tool>, <message>, <file path="...">
   │
   ├── FileTreeContextAdvisor (StreamAdvisor)
   │     → Intercepts the request BEFORE it reaches the LLM
   │     → Fetches the live file tree from MinIO for this project
   │     → Injects it as an additional SystemMessage: "---- FILE_TREE ---- [...]"
   │
   ├── CodeGenerationTools (@Tool methods)
   │     → Exposes read_files(paths: List<String>) as an LLM tool
   │     → LLM calls this to read specific file contents from MinIO before editing
   │
   └── User message

5. chatClient.stream().chatResponse() → Flux<ChatResponse>
   Each chunk is filtered and mapped to StreamResponse(text)
   → Emitted to the client as SSE tokens in real time

6. On stream completion (doOnComplete), a background task fires:
   ├── LlmResponseParser.parseChatEvents(fullText)
   │     → Extracts structured events from the LLM's XML output:
   │         <tool args="...">   → ChatEventType.TOOL_CALL
   │         <message phase="..."> → ChatEventType.MESSAGE
   │         <file path="...">  → ChatEventType.FILE_EDIT
   │
   ├── FILE_EDIT events → ProjectFileService.saveFile(projectId, path, content)
   │     → Writes updated file content to MinIO under projects/{projectId}/
   │
   ├── ChatMessages (USER + ASSISTANT) saved to PostgreSQL with token counts
   ├── ChatEvents saved (THOUGHT, TOOL_CALL, MESSAGE, FILE_EDIT) with sequenceOrder
   └── UsageService.recordTokenUsage(userId, totalTokens) → daily quota tracking
```

**LLM Output Example (what the AI returns):**
```xml
<message phase="start">I'll add a dark mode toggle. Let me check your current layout.</message>
<tool args="src/components/Header.tsx">Reading Header.tsx...</tool>
<!-- LLM calls read_files tool, receives file content -->
<message phase="planning">I need to modify Header.tsx to add a ThemeToggle and update App.tsx to wire up state.</message>
<file path="src/components/Header.tsx">
  ... full updated file content ...
</file>
<file path="src/App.tsx">
  ... full updated file content ...
</file>
<message phase="completed">Done! Added a sun/moon toggle to the header that switches between DaisyUI light and dark themes.</message>
```

---

### 🗂 Project File Management

All project files are stored in MinIO under the key pattern:

```
projects/{projectId}/src/App.tsx
projects/{projectId}/src/components/Header.tsx
projects/{projectId}/package.json
...
```

- **`GET /api/files/{projectId}/tree`** — Returns the full file tree (recursive directory listing from MinIO)
- **`GET /api/files/{projectId}/content?path=src/App.tsx`** — Returns raw file content
- **`POST /api/files/{projectId}`** — Manual file upload/create
- **`DELETE /api/files/{projectId}?path=src/old.tsx`** — Delete a file

When a project is first created, `ProjectTemplateService` bootstraps it with a starter Vite + React + TypeScript + Tailwind template by writing the initial files to MinIO.

---

### 🚀 Preview & Deployment Flow

When a user clicks **"Preview"** or **"Deploy"**:

```
POST /api/projects/{projectId}/deploy

KubernetesDeploymentServiceImpl.deploy(projectId)

Step 1 — Check for existing pod
  └── Query K8s namespace "lovable-app" for pods with labels:
        project-id={projectId}, status=busy
      If found → skip to Step 4 (re-register route)

Step 2 — Claim an idle runner pod
  └── List pods with label status=idle → pick first available
      If none → throw "No idle runners available. Scale up the runner-pool."
      Label the pod: status=busy, project-id={projectId}

Step 3 — Sync project files into the pod
  ├── syncer container (minio/mc sidecar):
  │     exec: "rm -rf /app/* && mc mirror --overwrite myminio/projects/{id}/ /app/"
  │     → Pulls ALL project files from MinIO into the shared /app volume
  │
  │     exec: "nohup mc mirror --overwrite --watch myminio/projects/{id}/ /app/ &"
  │     → Starts a file watcher: any future MinIO write auto-syncs to /app/
  │
  └── runner container (node:20-alpine):
        exec: "npm install && nohup npm run dev -- --host 0.0.0.0 --port 5173 &"
        → Installs deps and starts the Vite dev server

Step 4 — Register route in Redis
  └── Redis SET "route:project-{id}.app.domain.com" → "{podIP}:5173"  TTL: 6h

Step 5 — Return preview URL
  └── Response: { "url": "http://project-42.app.domain.com:8090" }
```

**Live reload:** Because `mc mirror --watch` continuously syncs MinIO → `/app/`, every AI-generated file save is picked up by Vite's HMR. The browser preview updates **automatically** after each chat turn — no redeploy needed.

---

### 🔑 Authentication Flow

```
POST /api/auth/signup   → Creates user, hashes password (BCrypt), returns JWT
POST /api/auth/login    → Validates credentials, returns JWT

All subsequent requests:
  Authorization: Bearer <jwt>
  → JwtAuthFilter validates token → sets SecurityContextHolder
  → @PreAuthorize("@security.canEditProject(#projectId)") guards endpoints
     checks ProjectMember table for role (OWNER / EDITOR / VIEWER)
```

---

### 💳 Billing & Subscription Flow

```
GET  /api/billing/plans              → List available plans (from DB)
POST /api/billing/checkout           → Creates Stripe Checkout Session → returns URL
POST /api/billing/portal             → Creates Stripe Customer Portal session
POST /api/billing/webhook            → Stripe webhook receiver (HMAC verified)
                                        Handles: checkout.session.completed,
                                                 customer.subscription.updated,
                                                 customer.subscription.deleted
GET  /api/usage/today                → Returns tokens used today vs. daily limit
GET  /api/usage/plan-limits          → Returns current plan's project/token limits
```

Subscription status gates project creation (`subscriptionService.canCreateNewProject()`) and token usage (`usageService.checkDailyTokensUsage()`).

---

### 👥 Project Collaboration Flow

```
POST /api/projects/{id}/members/invite   → Send invite (by email), assigns role
PUT  /api/projects/{id}/members/{uid}    → Change a member's role
DELETE /api/projects/{id}/members/{uid}  → Remove a member

Roles: OWNER > EDITOR > VIEWER
Permissions enforced via @PreAuthorize + SecurityExpressions bean
```

---

## 🧩 Key Design Decisions

| Decision | Rationale |
|---|---|
| **SSE (Server-Sent Events) for streaming** | Simpler than WebSockets for unidirectional LLM token streams; native `text/event-stream` support in browsers |
| **Spring AI `StreamAdvisor`** | `FileTreeContextAdvisor` intercepts the prompt pipeline reactively, enriching context without coupling to business logic |
| **LLM as XML writer** | Forces the model to emit structured `<file>`, `<message>`, `<tool>` tags, making deterministic parsing trivial |
| **MinIO as file store** | S3-compatible object storage that works locally and in prod (swap endpoint to AWS S3 without code changes) |
| **K8s runner pod pool** | Pre-warmed idle pods eliminate cold-start latency. Claiming a pod is just a label patch + exec — no new container spin-up |
| **Redis routing table** | The wildcard proxy avoids K8s Ingress complexity; subdomain routing is just a Redis `GET` |
| **`mc mirror --watch`** | Bridges the gap between MinIO (source of truth) and the running Vite server without a custom file-sync daemon |
| **MapStruct** | Compile-time DTO mapping avoids reflection overhead of ModelMapper and catches mapping errors at build time |

---

## 🌐 API Reference (Quick Summary)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/auth/signup` | Register a new user |
| `POST` | `/api/auth/login` | Login, get JWT |
| `GET` | `/api/auth/me` | Get current user profile |
| `POST` | `/api/chat/stream` | **Stream AI code generation (SSE)** |
| `GET` | `/api/chat/projects/{id}` | Get chat history for a project |
| `POST` | `/api/projects` | Create a new project |
| `GET` | `/api/projects` | List all projects for current user |
| `GET` | `/api/projects/{id}` | Get project details |
| `DELETE` | `/api/projects/{id}` | Delete a project |
| `POST` | `/api/projects/{id}/deploy` | **Deploy project to K8s runner** |
| `GET` | `/api/files/{id}/tree` | Get project file tree |
| `GET` | `/api/files/{id}/content` | Get file content by path |
| `POST` | `/api/files/{id}` | Create/update a file manually |
| `GET` | `/api/billing/plans` | List subscription plans |
| `POST` | `/api/billing/checkout` | Start Stripe checkout |
| `POST` | `/api/billing/webhook` | Stripe webhook endpoint |
| `GET` | `/api/usage/today` | Today's token usage |

Full interactive docs available at `/swagger-ui.html` when the app is running.

---

## 🐳 Kubernetes Setup (Preview Infrastructure)

### Create a kind Cluster

```bash
# Install kind (macOS)
brew install kind

# Create a cluster
kind create cluster --name lovable

# Verify kubectl is pointing at it
kubectl cluster-info --context kind-lovable
```

### Apply the Manifests

Apply the manifests to your cluster in order:

```bash
# 1. Create namespace + infra services (Redis, MinIO ExternalName, Proxy)
kubectl apply -f k8s/infra.yml

# 2. Deploy the runner pod pool (2 idle pods by default)
kubectl apply -f k8s/runner-pods.yml

# 3. Deploy the Node.js wildcard proxy
kubectl apply -f k8s/lovable-proxy.yml

# 4. Apply RBAC policies
kubectl apply -f k8s/policy.yml
```

> The runner pods live in the `lovable-app` namespace. Scale the pool up with:
> ```bash
> kubectl scale deployment runner-pool -n lovable-app --replicas=5
> ```

### Port-Forward Required Services

The Spring Boot application (running on your host) needs to talk to **Redis** running inside the cluster. The wildcard proxy also needs to be reachable from your browser. You must port-forward both:

**Terminal 1 — Redis (used by Spring Boot for usage cache and by the proxy for routing table):**
```bash
kubectl port-forward -n lovable-app svc/redis-service 6379:6379
```

**Terminal 2 — Node.js Wildcard Proxy (used to serve live previews in the browser):**
```bash
kubectl port-forward -n lovable-app svc/lovable-proxy-service 8090:80
```

> Keep both port-forward processes running for the full duration of your dev session. If either one dies, restart it with the same command.

After port-forwarding the proxy, preview URLs returned by `/api/projects/{id}/deploy` will be accessible at `http://project-{id}.app.domain.com:8090` from your browser (assuming you've added the hosts entries below).

### Wildcard DNS (Local)

For the wildcard subdomain routing to work locally, add entries to `/etc/hosts`:
```
127.0.0.1  project-1.app.domain.com
127.0.0.1  project-2.app.domain.com
```

Add one line per project ID you want to preview. For a more convenient setup, configure a local DNS wildcard resolver like `dnsmasq`:

```bash
# macOS with dnsmasq
brew install dnsmasq
echo "address=/.app.domain.com/127.0.0.1" >> /usr/local/etc/dnsmasq.conf
sudo brew services start dnsmasq
```

---

## 🔧 Environment Variables Summary

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:9010/pgvector-test` | PostgreSQL connection |
| `SPRING_AI_OPENAI_API_KEY` | — | OpenAI API key |
| `JWT_SECRET_KEY` | — | HS256 signing secret (≥256 bits) |
| `STRIPE_API_SECRET` | — | Stripe secret key |
| `STRIPE_WEBHOOK_SECRET` | — | Stripe webhook signing secret |
| `MINIO_URL` | `http://localhost:9000` | MinIO endpoint |
| `MINIO_ACCESS_KEY` | `minioadmin` | MinIO access key |
| `MINIO_SECRET_KEY` | `minioadmin123` | MinIO secret key |
| `SPRING_DATA_REDIS_HOST` | `localhost` | Redis host |
| `REDIS_URL` | `redis://redis-service:6379` | Redis URL (proxy service) |
| `CLIENT_URL` | `http://localhost:8080` | CORS allowed origin |

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 📸 Architecture Diagrams

The repository includes two reference diagrams:

- `code_execution_system_architecture_95ee32b790.png` — System-level view of the code execution pipeline
- `ER_Diagram_28d59734ef.png` — Entity-Relationship diagram for the database schema

---

## 👤 Author

**Shashi** — [@shashi162003](https://github.com/shashi162003)
