# Use a 32-bit JRE runtime image
FROM openjdk:8-jdk-alpine

WORKDIR /app

ARG JAR_FILE
# Copy the pre-built JAR file into the container
COPY ${JAR_FILE} app.jar

# Set entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
