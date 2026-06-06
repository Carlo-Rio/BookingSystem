# ─── Stage 1: Build ────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

# add --no-transfer-progress to see cleaner output
RUN mvn clean package -DskipTests --no-transfer-progress

# verify JAR was created
RUN ls -la /app/target/

# ─── Stage 2: Run ──────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S bookinggroup && adduser -S bookinguser -G bookinggroup

COPY --from=build /app/target/*.jar app.jar

RUN chown bookinguser:bookinggroup app.jar

USER bookinguser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]