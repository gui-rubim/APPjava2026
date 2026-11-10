package com.promptplace.model;

/**
 * Um texto nas duas línguas do app (ex.: o título de um prompt).
 * ENCAPSULAMENTO: quem usa só chama get() e recebe a versão do idioma atual.
 */
public class Texto {

    private final String ingles;
    private final String portugues;

    public Texto(String ingles, String portugues) {
        this.ingles = ingles;
        this.portugues = portugues;
    }

    /** Para textos digitados pelo usuário, que ficam iguais nos dois idiomas. */
    public Texto(String mesmoTexto) {
        this(mesmoTexto, mesmoTexto);
    }

    public String get() {
        return Idioma.t(ingles, portugues);
    }

    @Override
    public String toString() {
        return get();
    }
}
