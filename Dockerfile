# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Descargar dependencias (cache)
RUN mvn dependency:go-offline -B
COPY src ./src
# Compilar el proyecto empaquetando el JAR
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/medical-appointment-api-1.0.0.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Comando de ejecución (Usa H2 por defecto para despliegue sin configuración extra)
ENTRYPOINT ["java", "-jar", "app.jar"]
