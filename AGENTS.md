# Buratino Agent Guide

## Project
Kotlin + Spring Boot 3.4.5 (Java 21) | Maven | PostgreSQL + R2DBC | Tinkoff API

## Dev Commands
- `mvn clean package -DskipTests` - build without tests
- `mvn test` - run all tests
- `mvn spring-boot:run` - run locally
- `docker-compose -f ./docker/docker-compose.yml up -d` - start PostgreSQL

## Architecture
- 3-tier package structure: modules → stereotypes → classes
- Key modules: `account`, `assignment`, `order`, `price`, `instrument`, `security`
- `assignment` = core trading strategy logic
- Stereotypes per module: `adapter`, `controller`, `dao`, `dto`, `exception`, `model`, `service`
- Entry: `BuratinoApplication.kt`

## Database
- Flyway migrations: `src/main/resources/db/migration/`
- Local: `localhost:5432` (from docker-compose)
- Production: Yandex Cloud PostgreSQL

## Required Env Vars
`TINKOFF_API_TOKEN`, `ANONYMOUS_PASSWORD`, `ACCOUNT_NAME`, `POSTGRES_PASSWORD` (prod)

## Code Style
- Explicit variable types except constructor call
- Named arguments when calling functions
- 1 blank line after class definition
- Minimal comments - self-documenting code preferred

## Testing
- Integration tests mock Tinkoff API
- Config: `src/test/resources/application-test.properties`
