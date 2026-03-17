# ================================
# Stage 1 — Build the application
# ================================
FROM maven:3.9.12-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first (for dependency caching)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the JAR file (skip tests for faster build)
RUN mvn clean package -DskipTests

# ================================
# Stage 2 — Run the application
# ================================
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
