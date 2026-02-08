# Generic Dockerfile for any Cartigo service module
# Usage example:
#   docker build -f Dockerfile.service --build-arg SERVICE=cartigo-auth-service -t cartigo-auth-service:latest .

ARG SERVICE

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# Copy the whole repo (simple + reliable). For faster builds you can optimize later.
COPY . .

# Build only the requested module (and its dependencies)
RUN mvn -q -DskipTests -pl ${SERVICE} -am package

FROM eclipse-temurin:17-jre
WORKDIR /app

ARG SERVICE

# Copy the built jar (Spring Boot repackage output)
COPY --from=build /build/${SERVICE}/target/*.jar /app/app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
