FROM eclipse-temurin:21-jre
WORKDIR api_app
COPY target/*.jar api_app.jar
ENTRYPOINT ["java", "-jar", "api_app.jar"]
EXPOSE 8080