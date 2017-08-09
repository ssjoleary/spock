FROM openjdk:8-jdk-alpine
COPY target/spock-standalone.jar spock-standalone.jar
ENV PORT 3000
EXPOSE 3000
CMD ["java", "-jar", "spock-standalone.jar"]
