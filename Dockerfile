FROM openjdk:11
RUN ./gradlew build
ARG JAR_FILE=build/libs/kms-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

