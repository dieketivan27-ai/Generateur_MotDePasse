package com.generateur.cli;

/**
 * Objet de configuration immuable.
 * L'utilisation d'un 'record' (Java 16+) garantit que les paramètres 
 * ne peuvent pas être modifiés après la saisie utilisateur.
 */
public record CliConfig(
        int length,
        boolean useUpper,
        boolean useLower,
        boolean useDigits,
        boolean useSymbols,
        int count,
        boolean useDocker
) {
    /**
     * Le constructeur compact permet de valider les invariants métier 
     * immédiatement lors de la création de l'objet.
     */
    public CliConfig {
        if (length < 4 || length > 128) {
            throw new IllegalArgumentException("La longueur doit être comprise entre 4 et 128.");
        }
        if (count < 1 || count > 100) {
            throw new IllegalArgumentException("Le nombre de mots de passe doit être entre 1 et 100.");
        }
        if (!useUpper && !useLower && !useDigits && !useSymbols) {
            throw new IllegalArgumentException("Au moins un type de caractère doit être sélectionné.");
        }
    }
}
