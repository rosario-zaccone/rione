FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

ARG SERVICE_PATH

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY services services

RUN ./mvnw -B -pl "${SERVICE_PATH}" -am package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

ARG SERVICE_PATH

COPY --from=build /workspace/${SERVICE_PATH}/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
