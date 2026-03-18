FROM eclipse-temurin:21-jre
WORKDIR /api_gateway
COPY target/*.jar api_gateway.jar
ENTRYPOINT ["java", "-jar", "api_gateway.jar"]
EXPOSE 9090