# Account Service

Baseline Spring Boot microservice for account data in the Fiserv UBA demo.

## Build

```bash
mvn clean package
```

## Run

```bash
java -jar target/account-service-0.0.1-SNAPSHOT.jar
```

By default the service starts on port `8081` and reads profile settings from
`src/main/resources/application-dev.yml`.
