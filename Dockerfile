FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --uid 1001 kuzahealth
COPY --from=builder /app/target/kuzahealth-0.0.1-SNAPSHOT.jar app.jar
USER kuzahealth
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
