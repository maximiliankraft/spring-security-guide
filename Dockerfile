FROM gradle:8.0.2-jdk17-alpine as build

WORKDIR /buildenv

COPY build.gradle build.gradle
COPY settings.gradle settings.gradle

RUN gradle init

# dateien vom aktuellen verzeichnis in den container kopieren
COPY . .

# build gradle jar file
RUN gradle bootJar

RUN ls /buildenv/build/libs/


FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY --from=build /buildenv/build/libs/restsec-0.0.1-SNAPSHOT.jar /app/restsec-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/restsec-0.0.1-SNAPSHOT.jar"]