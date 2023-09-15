FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

COPY ./*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]