FROM mcr.microsoft.com/java/jdk:11u8-zulu-alpine as builder

WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew build

FROM mcr.microsoft.com/java/jre-headless:11u8-zulu-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/reactor-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]