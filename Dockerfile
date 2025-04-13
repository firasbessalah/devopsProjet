FROM openjdk:8-jdk-alpine 

ADD http://192.168.225.129:8081/repository/maven-snapshots/tn/esprit/spring/kaddem/0.0.1-SNAPSHOT/kaddem-0.0.1-20250413.004206-2.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]
