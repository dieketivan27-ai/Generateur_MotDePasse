package com.generateur.core;

import com.generateur.cli.CliConfig;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Moteur de génération de secrets aléatoires.
 * Utilise SecureRandom pour garantir une qualité cryptographique des tirages.
 */
public class PasswordGenerator {

    // Définition des pools de caractères.
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS    = "0123456789";
    private static final String SYMBOLS   = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    private final CliConfig config;
    
    /**
     * Utilisation de SecureRandom au lieu de Random.
     * Random est prédictible car basé sur l'horloge système (Lcg).
     * SecureRandom utilise des sources d'entropie du système (OS), 
     * rendant le secret mathématiquement imprévisible.
     */
    private final SecureRandom random = new SecureRandom();

    public PasswordGenerator(CliConfig config) {
        this.config = config;
    }

    public List<String> generate() {
        List<String> passwords = new ArrayList<>(config.count());
        for (int i = 0; i < config.count(); i++) {
            passwords.add(generateOne());
        }
        return Collections.unmodifiableList(passwords);
    }

    /**
     * Algorithme en 3 étapes pour garantir la conformité et l'aléa :
     * 1. Garantie d'inclusion : Force au moins un caractère de chaque type demandé.
     * 2. Remplissage : Complète jusqu'à la longueur souhaitée.
     * 3. Mélange (Shuffle) : Évite que les caractères "forcés" soient toujours au début.
     */
    private String generateOne() {
        String charset = buildCharset();
        List<Character> chars = new ArrayList<>(config.length());

        // Étape 1 : On s'assure que le mot de passe respecte les critères de diversité.
        if (config.useLower()   && chars.size() < config.length()) chars.add(randomChar(LOWERCASE));
        if (config.useUpper()   && chars.size() < config.length()) chars.add(randomChar(UPPERCASE));
        if (config.useDigits()  && chars.size() < config.length()) chars.add(randomChar(DIGITS));
        if (config.useSymbols() && chars.size() < config.length()) chars.add(randomChar(SYMBOLS));

        // Étape 2 : On complète aléatoirement.
        while (chars.size() < config.length()) {
            chars.add(randomChar(charset));
        }

        // Étape 3 : Le mélange est crucial. Sans cela, le pattern [Min][Maj][Chiffre][Special]
        // serait prédictible pour un attaquant, même si les caractères eux-mêmes sont aléatoires.
        Collections.shuffle(chars, random);
        
        StringBuilder sb = new StringBuilder(config.length());
        chars.forEach(sb::append);
        return sb.toString();
    }

    private String buildCharset() {
        StringBuilder sb = new StringBuilder();
        if (config.useLower())   sb.append(LOWERCASE);
        if (config.useUpper())   sb.append(UPPERCASE);
        if (config.useDigits())  sb.append(DIGITS);
        if (config.useSymbols()) sb.append(SYMBOLS);
        return sb.toString();
    }

    private char randomChar(String pool) {
        return pool.charAt(random.nextInt(pool.length()));
    }
}
