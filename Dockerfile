# Use Java 17 (same as your project)
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy jar
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose port
EXPOSE 5678

# Run app
ENTRYPOINT ["java","-jar","/app/app.jar"]
