# --- Étape 1 : Build ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# --- Étape 2 : Runtime ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/generateur.jar .

# Commande par défaut (affiche l'aide si aucun argument)
ENTRYPOINT ["java", "-jar", "generateur.jar"]
