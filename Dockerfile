FROM openjdk:21-jdk-slim

COPY target/connectdeaf-0.0.1-SNAPSHOT.jar /app/connectdeaf.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/connectdeaf.jar"]
