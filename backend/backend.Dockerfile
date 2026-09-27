FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY . .

# кэш из .m2 и упаковка jar
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /build/app/target/app-*.jar app.jar

ENV JAVA_OPTS=""

EXPOSE 8080 5005

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
