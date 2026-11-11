package com.promptplace.model;

/** As 5 etapas de uma encomenda, na ordem. */
public enum EtapaEncomenda {
    PEDIDO_ENVIADO(new Texto("Order sent", "Pedido enviado")),
    PROPOSTA_ACEITA(new Texto("Proposal accepted", "Proposta aceita")),
    EM_PRODUCAO(new Texto("In production", "Em produção")),
    ENTREGUE(new Texto("Delivered", "Entregue")),
    CONCLUIDO(new Texto("Completed", "Concluído"));

    private final Texto nome;

    EtapaEncomenda(Texto nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome.get();
    }
}
