# Use the OpenJDK 23 base image (or a compatible one)
FROM openjdk:23-jdk-slim

WORKDIR /app

# Copy the jar file to the container
COPY target/csvtojson-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
