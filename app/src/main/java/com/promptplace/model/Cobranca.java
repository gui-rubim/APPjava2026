package com.promptplace.model;

/** Cobrança mensal ou anual (no anual há 20% de desconto). */
public enum Cobranca {
    MENSAL(new Texto("monthly", "mensal")),
    ANUAL(new Texto("yearly", "anual"));

    public static final double DESCONTO_ANUAL = 0.20;

    private final Texto nome;

    Cobranca(Texto nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome.get();
    }
}
