package com.medico.backend.util;

import java.security.SecureRandom;
import java.util.Random;

public class GeneradorCodigo {

    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    /**
     * Genera un código único con formato: PREFIJO-NUMEROS-ALFANUMERICO
     * Ejemplo: US-482-X9J2M
     * * @param prefijo El prefijo del modelo (ej: US, MED, CT)
     * @return Código generado
     */
    public static String generarCodigo(String prefijo) {
        // Parte 2: 3 números aleatorios (000 - 999)
        int parteNumerica = RANDOM.nextInt(1000);
        String segmento2 = String.format("%03d", parteNumerica);

        // Parte 3: 5 caracteres alfanuméricos
        StringBuilder segmento3 = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            segmento3.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        // Resultado final concatenado
        return String.format("%s-%s-%s", prefijo, segmento2, segmento3.toString());
    }
}