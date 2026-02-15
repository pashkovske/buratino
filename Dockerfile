FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre AS production-stage
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

RUN mkdir -p /root/.postgresql && \
    wget "https://storage.yandexcloud.net/cloud-certs/CA.pem" \
         --output-document /root/.postgresql/root.crt && \
    chmod 0600 /root/.postgresql/root.crt

RUN keytool -importcert -noprompt \
    -alias yandex-cloud-ca \
    -file /root/.postgresql/root.crt \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit

EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
