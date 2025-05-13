FROM eclipse-temurin:17 AS builder
WORKDIR /app/
COPY . .
RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:17-jre
WORKDIR /app/
COPY --from=builder /app/target/CharityBoxes-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]