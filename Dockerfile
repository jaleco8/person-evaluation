# Dockerfile multi-stage para aplicación Spring Boot + Kogito
# Etapa 1: Build
FROM gradle:8.10.2-jdk21-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY gradle.properties .
COPY settings.gradle.kts .
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ gradle/

# Copiar código fuente
COPY app/ app/

# Ejecutar build (sin tests para optimizar)
RUN ./gradlew build -x test --no-daemon

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Crear usuario no-root
RUN addgroup -S spring && adduser -S -G spring spring

# Instalar dependencias del sistema
RUN apk add --no-cache curl

# Establecer directorio de trabajo
WORKDIR /app

# Cambiar al usuario no-root
USER spring:spring

# Copiar el jar desde la etapa de build
COPY --from=build --chown=spring:spring /app/app/build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]