# syntax=docker/dockerfile:1
# Multi-stage build: compile in a Maven+JDK21 image, run on a slim JRE21 image.
#
# This is the SINGLE DOCKER RECIPE for every microservice. Each service root
# holds a copy of this file with its own SERVER_PORT/EXPOSE substituted, so
# developers and the CI/CD pipeline (AWS CodePipeline + CodeBuild + ECR, all
# under ./ci-cd-aws) always build the exact same image.
#
# It deliberately uses the system `mvn` from the builder image rather than the
# ./mvnw wrapper: several services ship mvnw scripts without
# .mvn/wrapper/maven-wrapper.properties, so the wrapper cannot bootstrap itself.

# ---- Stage 1: build the executable jar ----
ARG BUILDER_BASE=maven:3.9-eclipse-temurin-21-alpine
FROM ${BUILDER_BASE} AS builder

WORKDIR /app

# Copy pom first so dependency layers are cached independently of source changes
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -DskipTests dependency:go-offline || true

COPY src src
# Compile & package for real (unit tests already ran in the CI Maven phase)
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -DskipTests package

# Fail fast: a service that produces no jar must never ship an empty image.
RUN ls -1 target/*.jar >/dev/null 2>&1 || { echo "No jar produced in target/"; ls -la target 2>/dev/null || true; exit 1; }

# ---- Stage 2: minimal well-known runtime image (Docker Official Image) ----
ARG RUNTIME_BASE=eclipse-temurin:21-jre-alpine
FROM ${RUNTIME_BASE}

# Spring Boot relaxed binding: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE can be
# overridden at container runtime, e.g. to point at the real registry DNS name.
ENV HOST=0.0.0.0 \
    SERVER_PORT=8080 \
    EUREKA_INSTANCE_HOSTNAME=EurekaServer \
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://EurekaServer:8070/eureka/ \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -Djava.security.egd=file:/dev/./urandom"

WORKDIR /app

COPY --link --from=builder /app/target/*.jar app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget -qO- "http://${HOST}:${SERVER_PORT}/actuator/health" >/dev/null 2>&1 || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]