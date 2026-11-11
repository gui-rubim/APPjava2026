package com.promptplace.model;

/**
 * Encomenda de um prompt sob medida (comissão personalizada).
 * COMPOSIÇÃO: a Encomenda cria a sua própria Comissao, que não existe sem ela.
 */
public class Encomenda {

    private final Criador criador; // ASSOCIAÇÃO
    private final String briefing;
    private final double orcamento;
    private final int prazoDias;
    private EtapaEncomenda etapa = EtapaEncomenda.PEDIDO_ENVIADO;
    private final Comissao comissao;

    public Encomenda(Criador criador, String briefing, double orcamento, int prazoDias) {
        if (briefing.trim().isEmpty()) {
            throw new IllegalArgumentException(Idioma.t(
                    "Describe what you need in the brief.", "Descreva o que você precisa no briefing."));
        }
        this.criador = criador;
        this.briefing = briefing.trim();
        this.orcamento = orcamento;
        this.prazoDias = prazoDias;
        this.comissao = new Comissao(this);
    }

    /** Vai para a próxima etapa. Ao concluir, o pagamento é liberado ao criador. */
    public void avancar() {
        if (isConcluida()) {
            return;
        }
        etapa = EtapaEncomenda.values()[etapa.ordinal() + 1];
        if (isConcluida()) {
            comissao.liberar();
        }
    }

    public boolean isConcluida() {
        return etapa == EtapaEncomenda.CONCLUIDO;
    }

    public Criador getCriador() {
        return criador;
    }

    public String getBriefing() {
        return briefing;
    }

    public double getOrcamento() {
        return orcamento;
    }

    public int getPrazoDias() {
        return prazoDias;
    }

    public EtapaEncomenda getEtapa() {
        return etapa;
    }

    public Comissao getComissao() {
        return comissao;
    }
}
