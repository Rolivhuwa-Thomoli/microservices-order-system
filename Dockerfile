# One Dockerfile for every service.
# First build the jars with: mvn package
# docker-compose passes the service name in SERVICE.
FROM eclipse-temurin:17-jre
ARG SERVICE
WORKDIR /app
COPY ${SERVICE}/target/${SERVICE}.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
