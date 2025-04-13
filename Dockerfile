
FROM eclipse-temurin:11-jdk-alpine

# Expose the application port
EXPOSE 8082

# Define the Nexus URL for the JAR
ENV NEXUS_URL="http://192.168.1.19:8081/repository/maven-releases/tn/esprit/spring/kaddem/0.0.1/kaddem-0.0.1.jar"

# Download the JAR from Nexus with error handling
RUN wget --tries=3 --timeout=10 -O /kaddem-0.0.1.jar "$NEXUS_URL" || { echo "Failed to download JAR from Nexus"; exit 1; }

# Run the application
ENTRYPOINT ["java", "-jar", "/kaddem-0.0.1.jar"]