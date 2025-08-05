# Use a 32-bit JRE runtime image
FROM eclipse-temurin:8-jre-slim-32bit

WORKDIR /app

# Copy the pre-built JAR file into the container
COPY target/OMD-0.0.1-SNAPSHOT.jar app.jar

# Set entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
