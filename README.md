# TravelApp — Backend

Monorepo Maven multi-módulo con arquitectura hexagonal (Ports & Adapters).
Java 21 · Spring Boot 3.3.0 · PostgreSQL · Redis · Keycloak

## Estructura de módulos

```
backend/
├── travel-domain/          # Núcleo de negocio puro — sin Spring, sin JPA
│   └── src/main/java/com/travelapp/
│       ├── trips/          # Aggregate Trip: domain/, ports/, usecases/
│       ├── events/         # Aggregate TravelEvent (vuelos, hoteles, actividades…)
│       ├── budget/         # Aggregate Budget + BudgetItem
│       ├── users/          # Aggregate User + UserPreferences
│       ├── documents/      # TravelDocument — PDFs subidos y parseados
│       ├── wishlist/       # WishlistItem
│       ├── gaps/           # TripGap — huecos detectados en el itinerario
│       ├── payment/        # PaymentMethod + report
│       ├── notifications/  # Puerto NotificationSender
│       ├── export/         # Puerto ItineraryExporter
│       ├── ai/             # Puerto AIProvider + value objects
│       └── shared/         # AggregateRoot, Money, excepciones de dominio
│
├── travel-infrastructure/  # Adaptadores JPA, web, seguridad y exportación
│   └── src/main/java/com/travelapp/
│       ├── TravelAppApplication.java      ← @SpringBootApplication
│       ├── persistence/                   # Entities, JpaRepositories, Mappers, Adapters
│       │   ├── entities/                  # BudgetEntity, EventEntity, TripGapEntity…
│       │   ├── mappers/                   # MapStruct: TripMapper, EventMapper, UserMapper…
│       │   └── repositories/             # JpaRepository + RepositoryAdapter por aggregate
│       ├── adapters/
│       │   ├── export/                    # WeasyPrintExporter
│       │   └── storage/                  # S3StorageAdapter (Cloudflare R2)
│       ├── web/
│       │   ├── controllers/              # 10 @RestController (implementan interfaces generadas)
│       │   ├── dto/                      # DTOs de web (request/response locales)
│       │   ├── mappers/                  # TripDtoMapper, EventDtoMapper
│       │   └── security/                 # SecurityConfig, KeycloakRoleConverter
│       ├── config/                        # WebClientConfig, ResilienceConfig (Resilience4j)
│       ├── scheduler/                     # CancellationAlertJob, GapDetectionJob (Quartz)
│       └── resources/
│           └── db/migration/             # V1…V4 — Flyway (Postgres)
│
├── travel-ai/              # Adaptadores AI intercambiables
│   └── com/travelapp/ai/adapters/
│       ├── claude/         # ClaudeAdapter (prod — claude-sonnet-4-6)
│       ├── ollama/         # OllamaAdapter (local/piloto — llama3.2 / mistral:7b)
│       ├── http/           # HttpAIAdapter genérico + circuit breaker Resilience4j
│       ├── mock/           # MockAIAdapter (tests)
│       └── shared/         # PromptLibrary, ActivitySuggestionMapper, ParsedDocumentMapper
│
├── travel-notifications/   # Envío de notificaciones + Quartz scheduler
│   └── com/travelapp/notifications/
│       ├── adapters/       # NotificationSenderImpl (orquesta FCM + email)
│       ├── adapters/email/ # EmailAdapter
│       ├── adapters/fcm/   # FCMAdapter, PushSender
│       └── scheduler/      # CancellationAlertJob, GapDetectionJob
│
└── travel-api/             # Módulo de packaging — spring-boot-maven-plugin + contrato OpenAPI
    └── src/main/resources/
        ├── openapi/travelapp-api.yaml    ← fuente de verdad del contrato
        ├── application.yml               ← base (Keycloak, Redis, Flyway, OTEL, Prometheus)
        ├── application-local.yml         ← AI=Ollama, SQL debug
        ├── application-test.yml          ← AI=Mock, Testcontainers JDBC
        ├── application-pilot.yml         ← AI=Ollama, docker-compose IPs
        ├── application-k8s.yml           ← Istio mTLS, service mesh URLs
        └── application-prod.yml          ← AI=Claude, logs WARN
```

> **API First**: modifica siempre `travelapp-api.yaml` primero → compila → implementa.
> Las interfaces y DTOs generados en `target/generated-sources/openapi/` nunca se editan.

## Prerrequisitos locales

| Servicio | Versión mínima | Para qué |
|---|---|---|
| JDK | 21 | Compilar y ejecutar |
| Maven | 3.9+ | Build |
| Docker | 24+ | PostgreSQL, Redis, Keycloak, Ollama |
| PostgreSQL | 16 | Base de datos principal |
| Redis | 7 | Caché de sugerencias AI |
| Keycloak | 24 | Autenticación JWT |
| Ollama | última | Modelos AI en local (opcional) |

## Arranque local (paso a paso)

### 1. Infraestructura con Docker

Crea un `docker-compose.local.yml` con los servicios mínimos:

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: travelapp
      POSTGRES_USER: travelapp
      POSTGRES_PASSWORD: travelapp
    ports: ["5432:5432"]

  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]

  keycloak:
    image: quay.io/keycloak/keycloak:24.0.4
    command: start-dev
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports: ["8180:8080"]

  ollama:
    image: ollama/ollama
    ports: ["11434:11434"]
    volumes: ["ollama_data:/root/.ollama"]

volumes:
  ollama_data:
```

```bash
docker compose -f docker-compose.local.yml up -d
docker exec <ollama-container> ollama pull llama3.2   # primera vez
```

### 2. Keycloak — configurar realm

Accede a `http://localhost:8180` (admin / admin) y crea:
- Realm: `travelapp`
- Client: `travelapp-api` (confidential, con secret)
- Roles: `user`, `admin`

### 3. Compilar y arrancar

```bash
# Desde backend/
mvn clean install -DskipTests

# Arrancar con perfil local (Ollama como AI, SQL debug activo)
mvn spring-boot:run -pl travel-api -am -Dspring-boot.run.profiles=local
```

### 4. Verificar

```
Swagger UI:   http://localhost:8080/api/swagger-ui.html
Health:       http://localhost:8080/api/actuator/health
Metrics:      http://localhost:8080/api/actuator/prometheus
Keycloak:     http://localhost:8180
```

## Proveedores AI

| Perfil | Provider | Modelo | Cuándo |
|---|---|---|---|
| `local` | Ollama | llama3.2 | Desarrollo — sin coste |
| `test` | Mock | — | Tests automatizados |
| `pilot` | Ollama | mistral:7b | Piloto en docker-compose |
| `prod` | Claude | claude-sonnet-4-6 | Producción |

Selección vía variable de entorno `AI_PROVIDER=claude|ollama|mock`.

## Variables de entorno

| Variable | Local (default) | Piloto | Producción |
|---|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/travelapp` | `jdbc:postgresql://postgres:5432/travelapp` | Fly Postgres URL |
| `DB_USER` | `travelapp` | `travelapp` | secreto |
| `DB_PASSWORD` | `travelapp` | secreto | secreto |
| `REDIS_URL` | `redis://localhost:6379` | `redis://redis:6379` | secreto |
| `KEYCLOAK_URL` | `http://localhost:8180` | `http://keycloak:8080` | `https://auth.travelapp.com` |
| `KEYCLOAK_REALM` | `travelapp` | `travelapp` | `travelapp` |
| `AI_PROVIDER` | `ollama` | `ollama` | `claude` |
| `CLAUDE_API_KEY` | — | — | `sk-ant-…` |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4318` | collector interno | collector prod |

## Tests

```bash
mvn test                      # todos los módulos (AI=Mock, Testcontainers Postgres)
mvn test -pl travel-domain    # solo tests de dominio (sin Spring)
mvn test -pl travel-infrastructure  # tests de integración con Testcontainers
```

Los tests de integración levantan PostgreSQL automáticamente con Testcontainers
(el JDBC URL `jdbc:tc:postgresql:16:///travelapp` no necesita Docker manual).

## Observabilidad

| Herramienta | URL local | Qué expone |
|---|---|---|
| Actuator health | `/api/actuator/health` | Estado del servicio |
| Prometheus | `/api/actuator/prometheus` | Métricas para scraping |
| OTEL Collector | `localhost:4318` (OTLP) | Trazas distribuidas → Jaeger |

Configura el OTEL Collector + Jaeger para visualizar trazas localmente.

## Base de datos (Flyway)

| Migración | Contenido |
|---|---|
| `V1__init_schema.sql` | Tablas base: users, trips, events, flights, accommodations, budgets, budget_items, wishlist_items, feature_flags |
| `V2__purchase_status_and_payment_methods.sql` | payment_methods, enum purchase_status, campos de compra en flights/accommodations, vistas de reporte |
| `V3__gaps_document_types_exports.sql` | documents, trip_gaps, document_types, export_jobs, notification_log |
| `V4__missing_columns.sql` | Columnas faltantes: notes/estimated_cost/website_url en wishlist_items; notes en budget_items |

## CI/CD

El workflow `.github/workflows/travel-core.yml` ejecuta el build y los tests en cada push.
