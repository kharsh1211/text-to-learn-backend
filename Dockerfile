# Stage 1: Build stage
# Uses Maven and JDK 21 to compile the code
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src
# Build the application and skip tests for a faster deployment
RUN mvn clean package -DskipTests

# Stage 2: Run stage
# Uses a lightweight JRE (Java Runtime Environment) 21 to run the app
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copy only the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar
# Expose the port (8080 is standard, Render will override this via $PORT)
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]