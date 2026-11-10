package com.promptplace.model;

import java.util.Locale;

/**
 * Idiomas do app. Inglês é o principal; português é o secundário.
 * O idioma atual fica guardado aqui e as classes do model consultam ele.
 */
public enum Idioma {
    INGLES("en", Locale.US),
    PORTUGUES("pt", new Locale("pt", "BR"));

    // ENCAPSULAMENTO: o idioma atual só muda pelo método setAtual().
    private static Idioma atual = INGLES;

    private final String codigo;
    private final Locale locale;

    Idioma(String codigo, Locale locale) {
        this.codigo = codigo;
        this.locale = locale;
    }

    public static Idioma getAtual() {
        return atual;
    }

    public static void setAtual(Idioma idioma) {
        atual = idioma;
    }

    /** Procura o idioma pelo código ("en" ou "pt"). Se não achar, usa inglês. */
    public static Idioma porCodigo(String codigo) {
        for (Idioma i : values()) {
            if (i.codigo.equals(codigo)) {
                return i;
            }
        }
        return INGLES;
    }

    /** Escolhe entre o texto em inglês e em português conforme o idioma atual. */
    public static String t(String ingles, String portugues) {
        return atual == PORTUGUES ? portugues : ingles;
    }

    public String getCodigo() {
        return codigo;
    }

    public Locale getLocale() {
        return locale;
    }
}
