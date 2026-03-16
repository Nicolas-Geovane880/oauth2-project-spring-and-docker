FROM eclipse-temurin:21-jre
WORKDIR /auth_app
COPY target/*.jar auth_app.jar
ENTRYPOINT ["java", "-jar", "auth_app.jar"]
EXPOSE 9000
