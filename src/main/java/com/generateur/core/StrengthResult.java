package com.generateur.core;

public record StrengthResult(
        int score,
        String label,
        String crackTime,
        boolean fromDocker
) {}
