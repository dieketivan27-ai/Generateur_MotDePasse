package com.generateur.core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Audit de sécurité. Communique avec le conteneur externe 'scorer' via HTTP.
 */
public class StrengthChecker {

    // L'URL pointe vers le nom du service défini dans docker-compose
    private static final String SCORER_URL = "http://password-scorer:8080/validate";
    private final HttpClient httpClient;

    public StrengthChecker() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public StrengthResult check(String password) {
        try {
            // Tentative de validation via l'outil externe (Docker)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SCORER_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(password))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String[] parts = response.body().split("\\|");
                return new StrengthResult(
                    Integer.parseInt(parts[0]), 
                    parts[1], 
                    parts[2], 
                    true // Indique que la validation vient de Docker
                );
            }
        } catch (Exception e) {
            // En cas d'erreur (service Docker éteint), on utilise le fallback interne.
            System.err.println("[LOG] Service de validation Docker injoignable. Passage en mode local.");
        }

        return checkLocal(password);
    }

    /**
     * Algorithme de secours (fallback) si le conteneur de validation est indisponible.
     */
    private StrengthResult checkLocal(String password) {
        int score = (password.length() >= 12) ? 3 : 2;
        String label = switch (score) {
            case 0 -> "Très faible";
            case 1 -> "Faible";
            case 2 -> "Moyen";
            case 3 -> "Fort";
            default -> "Très fort";
        };

        // Simulation d'un temps de crack parlant pour le mode dégradé
        String crackTime = switch (score) {
            case 0, 1 -> "quelques secondes";
            case 2 -> "quelques minutes";
            case 3 -> "plusieurs heures";
            case 4 -> "plusieurs mois";
            default -> "plusieurs années";
        };

        return new StrengthResult(score, label, crackTime + " (estimation locale)", false);
    }
}
