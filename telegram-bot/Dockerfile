FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY .. .
RUN mvn -pl telegram-bot -am -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/telegram-bot/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]