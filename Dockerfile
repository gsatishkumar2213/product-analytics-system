FROM  maven:3.9.6-eclipse-temurin-21-alpine as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar product-view-service.jar
EXPOSE 9095
ENTRYPOINT ["java","-jar","product-view-service.jar"]