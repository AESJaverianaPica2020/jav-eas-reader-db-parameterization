FROM openjdk:11
ADD target/jav-eas-reader-db-parameterization-0.0.1-SNAPSHOT.jar jav-eas-reader-db-parameterization-0.0.1-SNAPSHOT.jar
EXPOSE 9071
ENTRYPOINT ["java", "-jar", "jav-eas-reader-db-parameterization-0.0.1-SNAPSHOT.jar"]
