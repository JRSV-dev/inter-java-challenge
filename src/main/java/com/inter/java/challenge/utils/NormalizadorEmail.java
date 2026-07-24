package com.inter.java.challenge.utils;

import java.util.Locale;
import java.util.Optional;

public final class NormalizadorEmail {

    public static String normalizar(String email) {
        return Optional.ofNullable(email)
                .map(String::trim)
                .map(valor -> valor.toLowerCase(Locale.ROOT))
                .orElse(null);
    }
}
