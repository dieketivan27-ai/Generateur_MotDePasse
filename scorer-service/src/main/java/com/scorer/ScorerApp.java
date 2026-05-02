package com.scorer;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Micro-service de validation de mots de passe.
 *
 * Ce service tourne dans un conteneur Docker isolé du générateur principal.
 * Il reçoit un mot de passe via HTTP POST, l'analyse avec la bibliothèque
 * zxcvbn4j (portage Java de l'algorithme Dropbox) et retourne un score de 0 à 4.
 *
 * Utilisation du HttpServer intégré au JDK pour éviter toute dépendance externe
 * supplémentaire (pas de Spring, pas de Tomcat).
 */
public class ScorerApp {

    public static void main(String[] args) throws IOException {
        // Le serveur écoute sur le port 8080, accessible uniquement via le réseau Docker interne.
        // Il n'est pas exposé à l'extérieur de Docker Compose.
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/validate", new ValidationHandler());

        // Endpoint de santé permettant au générateur de vérifier que le service est prêt
        server.createContext("/health", exchange -> {
            byte[] response = "OK".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        server.setExecutor(null);
        System.out.println("[SCORER] Service de validation zxcvbn démarré sur le port 8080...");
        server.start();
    }

    /**
     * Handler HTTP qui reçoit un mot de passe en texte brut (POST body)
     * et retourne le résultat de l'analyse zxcvbn au format "score|label|tempsCrack".
     *
     * Ce format pipe-délimité est plus léger qu'un JSON complet pour ce cas d'usage simple.
     */
    static class ValidationHandler implements HttpHandler {
        // Zxcvbn est thread-safe et coûteux à instancier : on le crée une seule fois.
        private final Zxcvbn zxcvbn = new Zxcvbn();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Lecture du mot de passe brut envoyé dans le corps de la requête HTTP
            String password = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            // Analyse via zxcvbn : détecte les patterns, dictionnaires et séquences de touches
            Strength strength = zxcvbn.measure(password);
            int score = strength.getScore(); // Score de 0 (très faible) à 4 (très fort)

            String label = switch (score) {
                case 0 -> "Très faible";
                case 1 -> "Faible";
                case 2 -> "Moyen";
                case 3 -> "Fort";
                default -> "Très fort";
            };

            // Strength expose getCrackTimesDisplay() directement (source Strength.java ligne 122).
            // CrackTimesDisplay.getOfflineSlowHashing1e4perSecond() retourne l'estimation en texte lisible.
            // Scénario choisi : "offline slow hashing" (bcrypt) — le plus représentatif des BDD modernes.
            String crackTime = strength.getCrackTimesDisplay()
                                       .getOfflineSlowHashing1e4perSecond();

            // Réponse au format "score|label|tempsCrack" — simple et sans dépendance JSON côté serveur
            String response = String.format("%d|%s|%s", score, label, crackTime);

            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
}
