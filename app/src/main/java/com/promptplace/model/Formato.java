package com.promptplace.model;

/** Formata números no padrão do idioma atual (inglês: 14.90, português: 14,90). */
public class Formato {

    public static String texto(String modelo, Object... valores) {
        return String.format(Idioma.getAtual().getLocale(), modelo, valores);
    }
}
