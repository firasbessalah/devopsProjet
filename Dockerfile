# Utilisation de l'image officielle Eclipse Temurin pour Java 11
FROM eclipse-temurin:11-jdk-alpine

# Métadonnées
LABEL maintainer="votre-email@example.com"
LABEL version="1.0"
LABEL description="Image Docker pour l'application Kaddem"

# Variables d'environnement configurables
ENV APP_PORT=8082 \
    APP_JAR_NAME="kaddem-0.0.1.jar" \
    NEXUS_BASE_URL="http://nexus:8081" \
    NEXUS_REPO_PATH="repository/maven-releases/tn/esprit/spring/kaddem/0.0.1"

# Installation des dépendances nécessaires
RUN apk add --no-cache \
    wget \
    && rm -rf /var/cache/apk/*

# Création du répertoire de travail
WORKDIR /app

# Téléchargement sécurisé du JAR depuis Nexus
RUN wget -q --show-progress \
    "${NEXUS_BASE_URL}/${NEXUS_REPO_PATH}/${APP_JAR_NAME}" \
    -O "${APP_JAR_NAME}" \
    && test -f "${APP_JAR_NAME}" \
    || { echo "ERREUR: Échec du téléchargement du JAR depuis Nexus"; exit 1; }

# Exposition du port (à des fins documentaires)
EXPOSE ${APP_PORT}

# Configuration de la JVM
ENV JAVA_OPTS="-Xms256m -Xmx512m -Dspring.profiles.active=prod"

# Point d'entrée avec paramètres optimisés
ENTRYPOINT exec java $JAVA_OPTS -jar "${APP_JAR_NAME}"

# Configuration santé (optionnelle)
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget -q --spider http://localhost:${APP_PORT}/actuator/health || exit 1