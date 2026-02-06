FROM eclipse-temurin:8-jre

WORKDIR /opt/app

COPY target/account-service.jar account-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "account-service.jar"]
