# ============================================================
# travelapp-backend — Multi-stage Dockerfile
# Imagen final: ~120MB (JRE Alpine)
# ============================================================

# ── Stage 1: dependencias Maven (cacheado si no cambia pom.xml) ──
FROM eclipse-temurin:21-jdk-alpine AS deps
WORKDIR /build

# Copiar solo los POMs primero — Docker cachea esta capa
# si solo cambia código fuente, no re-descarga dependencias
COPY pom.xml                            ./
COPY travel-domain/pom.xml             travel-domain/
COPY travel-infrastructure/pom.xml     travel-infrastructure/
COPY travel-ai/pom.xml                 travel-ai/
COPY travel-notifications/pom.xml      travel-notifications/
COPY travel-api/pom.xml                travel-api/

RUN mvn -B dependency:go-offline \
        -Dmaven.wagon.httpconnectionManager.ttlSeconds=25 \
        --no-transfer-progress \
    || true   # ignorar error si alguna dep no existe aún

# ── Stage 2: compilar y empaquetar ──────────────────────────
FROM deps AS builder
WORKDIR /build

# Copiar código fuente
COPY travel-domain/src             travel-domain/src/
COPY travel-infrastructure/src     travel-infrastructure/src/
COPY travel-ai/src                 travel-ai/src/
COPY travel-notifications/src      travel-notifications/src/
COPY travel-api/src                travel-api/src/

RUN mvn -B package \
        -DskipTests \
        -Dmaven.wagon.httpconnectionManager.ttlSeconds=25 \
        --no-transfer-progress

# ── Stage 3: imagen final mínima ────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S travelapp && adduser -S travelapp -G travelapp

# Copiar solo el JAR generado
COPY --from=builder /build/travel-api/target/*.jar app.jar

# Directorio para logs (montado como volumen en prod)
RUN mkdir -p /app/logs && chown -R travelapp:travelapp /app

USER travelapp

EXPOSE 8080

# JVM tuneada para containers:
# -XX:+UseContainerSupport      → detecta límites de memoria del container
# -XX:MaxRAMPercentage=75.0     → usa hasta el 75% de la RAM asignada
# -XX:+UseG1GC                  → GC optimizado para baja latencia
# -Djava.security.egd=...       → arranque más rápido en Linux
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
