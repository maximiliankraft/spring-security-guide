
FROM gradle:8.0.2-jdk17-alpine as build

WORKDIR /spring

COPY . .

RUN gradle bootJar -x test

FROM eclipse-temurin:17-alpine

COPY --from=build /spring/build/libs/restsec-0.0.1-SNAPSHOT.jar /restsec-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar" ,"/restsec-0.0.1-SNAPSHOT.jar"]

