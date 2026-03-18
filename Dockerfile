# Build stage
FROM maven:3.8.5-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage (FIXED IMAGE)
FROM eclipse-temurin:8-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]