package com.promptplace.model;

/** Os tipos de transação do histórico. */
public enum TipoTransacao {
    COMPRA(new Texto("Purchase", "Compra")),
    ASSINATURA(new Texto("Subscription", "Assinatura")),
    DOACAO(new Texto("Donation", "Doação")),
    COMISSAO(new Texto("Commission", "Comissão"));

    private final Texto nome;

    TipoTransacao(Texto nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome.get();
    }
}
