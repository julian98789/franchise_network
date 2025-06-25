FROM amazoncorretto:17.0.12

WORKDIR /app

COPY build/libs/franchise-0.0.1-SNAPSHOT.jar /app/franchise-0.0.1-SNAPSHOT.jar

EXPOSE 8085

CMD ["java", "-jar", "franchise-0.0.1-SNAPSHOT.jar"]