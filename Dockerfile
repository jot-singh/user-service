# Dockerfile for user-service
FROM maven:3.9.11-amazoncorretto-21 AS builder
WORKDIR /build
COPY pom.xml mvnw ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/user-service.jar"]
