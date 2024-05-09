FROM gradle:8.6-jdk17 AS build
WORKDIR /app
COPY . /app
RUN gradle clean build --no-daemon
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/cogo.jar
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "cogo.jar"]