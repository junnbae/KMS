FROM openjdk:11
ARG JAR_FILE=build/libs/kms-0.0.1-SNAPSHOT.jar
RUN chmod +x ./gradlew
RUN ./gradlew build 
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

