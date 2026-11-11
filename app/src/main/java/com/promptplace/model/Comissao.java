package com.promptplace.model;

/**
 * HERANÇA + POLIMORFISMO: pagamento de uma encomenda sob medida.
 * O dinheiro fica EM CUSTÓDIA até a entrega ser aprovada. Taxa de 10%.
 */
public class Comissao extends Transacao {

    public static final double TAXA = 0.10;

    private final Encomenda encomenda; // ASSOCIAÇÃO

    public Comissao(Encomenda encomenda) {
        super(encomenda.getOrcamento());
        this.encomenda = encomenda;
        this.situacao = Situacao.EM_CUSTODIA; // "situacao" é protected em Transacao
    }

    /** Libera o pagamento ao criador (quando a entrega é aprovada). */
    void liberar() {
        situacao = Situacao.CONCLUIDO;
    }

    @Override
    public TipoTransacao getTipo() {
        return TipoTransacao.COMISSAO;
    }

    @Override
    public String getItem() {
        return Idioma.t("Commission for ", "Comissão para ") + encomenda.getCriador().getNome();
    }

    @Override
    public double getTaxaPlataforma() {
        return TAXA;
    }
}
