package com.generateur.cli;

import java.util.Scanner;

/**
 * Gère l'interaction avec l'utilisateur.
 * Supporte deux modes : 
 * 1. Mode Script (Arguments) : Pour l'automatisation.
 * 2. Mode Interactif (Saisie) : Pour l'utilisateur final en terminal.
 */
public class CliParser {

    /**
     * Point de décision entre le mode arguments et le mode interactif.
     */
    public CliConfig parse(String[] args) {
        if (args.length == 0) {
            return runInteractive();
        }

        // Valeurs par défaut pour le mode script
        int length = 12;
        boolean upper = false, lower = true, digits = false, symbols = false;
        int count = 1;

        // Parsing manuel pour éviter d'ajouter des dépendances lourdes (ex: Apache Commons CLI).
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-l" -> length = Integer.parseInt(args[++i]);
                case "-u" -> upper = true;
                case "-lo" -> lower = true;
                case "-nlo" -> lower = false;
                case "-d" -> digits = true;
                case "-s" -> symbols = true;
                case "-n" -> count = Integer.parseInt(args[++i]);
                case "-h", "--help" -> {
                    showHelp();
                    System.exit(0);
                }
            }
        }

        return new CliConfig(length, upper, lower, digits, symbols, count, false);
    }

    /**
     * Mode interactif utilisant Scanner pour lire l'entrée standard (System.in).
     * Ce mode est déclenché quand aucun argument n'est fourni.
     */
    private CliConfig runInteractive() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n=== GÉNÉRATEUR DE MOTS DE PASSE ===");
            System.out.println("Prêt pour la configuration. Appuyez sur [ENTRÉE] pour débuter...");
            System.out.flush();
            
            // On attend une première validation pour synchroniser le terminal Docker
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            System.out.println("\n--- PARAMÈTRES DE GÉNÉRATION ---");
            
            System.out.println("1. Longueur du mot de passe (4-128) [défaut 12] :");
            System.out.print("Saisie : ");
            System.out.flush();
            String lStr = scanner.hasNextLine() ? scanner.nextLine() : "";
            int length = lStr.trim().isEmpty() ? 12 : Integer.parseInt(lStr.trim());

            System.out.println("2. Inclure des Majuscules ? (o/n) [défaut n] :");
            System.out.print("Saisie : ");
            System.out.flush();
            boolean upper = scanner.hasNextLine() && scanner.nextLine().equalsIgnoreCase("o");

            System.out.println("3. Inclure des Minuscules ? (o/n) [défaut o] :");
            System.out.print("Saisie : ");
            System.out.flush();
            String loStr = scanner.hasNextLine() ? scanner.nextLine() : "o";
            boolean lower = loStr.trim().isEmpty() || loStr.equalsIgnoreCase("o");

            System.out.println("4. Inclure des Chiffres ? (o/n) [défaut n] :");
            System.out.print("Saisie : ");
            System.out.flush();
            boolean digits = scanner.hasNextLine() && scanner.nextLine().equalsIgnoreCase("o");

            System.out.println("5. Inclure des Symboles ? (o/n) [défaut n] :");
            System.out.print("Saisie : ");
            System.out.flush();
            boolean symbols = scanner.hasNextLine() && scanner.nextLine().equalsIgnoreCase("o");

            System.out.println("6. Nombre de mots de passe à générer [défaut 1] :");
            System.out.print("Saisie : ");
            System.out.flush();
            String nStr = scanner.hasNextLine() ? scanner.nextLine() : "";
            int count = nStr.trim().isEmpty() ? 1 : Integer.parseInt(nStr.trim());

            return new CliConfig(length, upper, lower, digits, symbols, count, false);
        } catch (Exception e) {
            System.out.println("\n[WARN] Entrée invalide ou interruption. Utilisation des réglages par défaut (12 caractères, sécurisé).");
            return new CliConfig(12, true, true, true, false, 1, false);
        }
    }

    private void showHelp() {
        System.out.println("Usage: java -jar generateur.jar [options]");
        System.out.println("Options:");
        System.out.println("  -l <taille>  Longueur du mot de passe (4-128)");
        System.out.println("  -u           Inclure des majuscules");
        System.out.println("  -lo          Inclure des minuscules (actif par défaut)");
        System.out.println("  -nlo         Exclure les minuscules");
        System.out.println("  -d           Inclure des chiffres");
        System.out.println("  -s           Inclure des symboles");
        System.out.println("  -n <nombre>  Nombre de mots de passe (mode rafale)");
    }
}
