package com.promptplace.model;

/** Situação de uma transação. */
public enum Situacao {
    CONCLUIDO(new Texto("Completed", "Concluído")),
    EM_CUSTODIA(new Texto("In escrow", "Em custódia"));

    private final Texto nome;

    Situacao(Texto nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome.get();
    }
}
