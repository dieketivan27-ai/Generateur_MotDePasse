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
            System.out.println("=== Générateur de Mots de Passe (Mode Interactif) ===");
            
            System.out.print("Longueur (défaut 12) : ");
            String lStr = scanner.hasNextLine() ? scanner.nextLine() : "";
            int length = lStr.trim().isEmpty() ? 12 : Integer.parseInt(lStr.trim());

            System.out.print("Inclure Majuscules ? (o/n) : ");
            boolean upper = scanner.hasNextLine() && scanner.nextLine().equalsIgnoreCase("o");

            System.out.print("Inclure Chiffres ? (o/n) : ");
            boolean digits = scanner.hasNextLine() && scanner.nextLine().equalsIgnoreCase("o");

            System.out.print("Inclure Symboles ? (o/n) : ");
            boolean symbols = scanner.hasNextLine() && scanner.nextLine().equalsIgnoreCase("o");

            System.out.print("Nombre de mots de passe ? (défaut 1) : ");
            String nStr = scanner.hasNextLine() ? scanner.nextLine() : "";
            int count = nStr.trim().isEmpty() ? 1 : Integer.parseInt(nStr.trim());

            return new CliConfig(length, upper, true, digits, symbols, count, false);
        } catch (Exception e) {
            System.out.println("[WARN] Mode interactif indisponible ou interrompu. Utilisation des réglages par défaut.");
            return new CliConfig(12, true, true, true, false, 1, false);
        }
    }

    private void showHelp() {
        System.out.println("Usage: java -jar generateur.jar [options]");
        System.out.println("Options:");
        System.out.println("  -l <taille>  Longueur du mot de passe (4-128)");
        System.out.println("  -u           Inclure des majuscules");
        System.out.println("  -lo          Inclure des minuscules (actif par défaut)");
        System.out.println("  -d           Inclure des chiffres");
        System.out.println("  -s           Inclure des symboles");
        System.out.println("  -n <nombre>  Nombre de mots de passe (mode rafale)");
    }
}
