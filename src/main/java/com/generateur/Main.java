package com.generateur;

import com.generateur.cli.CliConfig;
import com.generateur.cli.CliParser;
import com.generateur.core.PasswordGenerator;
import com.generateur.core.StrengthChecker;
import com.generateur.core.StrengthResult;
import java.util.List;

/**
 * Orchestrateur principal de l'application.
 * Coordonne le parsing, la génération et l'affichage des résultats.
 */
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("[INFO] Démarrage du générateur...");
            CliParser parser = new CliParser();
            CliConfig config = parser.parse(args);

            System.out.println("[INFO] Paramètres : Longueur=" + config.length() + ", Nombre=" + config.count());
            
            PasswordGenerator generator = new PasswordGenerator(config);
            StrengthChecker checker = new StrengthChecker();

            System.out.println("[INFO] Génération des secrets en cours...");
            List<String> passwords = generator.generate();

            System.out.println("\n--- RÉSULTATS ---");
            if (passwords.isEmpty()) {
                System.out.println("(Aucun mot de passe généré)");
            }

            for (String pwd : passwords) {
                // Pour chaque mot de passe, on réalise un audit de sécurité immédiat.
                StrengthResult result = checker.check(pwd);
                
                System.out.printf("Mot de passe : %s\n", pwd);
                System.out.printf("Force        : %s (%d/4)\n", result.label(), result.score());
                System.out.printf("Temps crack  : %s\n", result.crackTime());
                System.out.println("-----------------");
                System.out.flush(); // Force l'affichage immédiat
            }
            System.out.println("[INFO] Terminé.");

        } catch (Exception e) {
            // Gestion d'erreur centralisée pour éviter les traces de pile (Stacktrace) verbeuses en CLI.
            System.err.println("ERREUR : " + e.getMessage());
            System.err.flush();
            System.exit(1);
        }
    }
}
