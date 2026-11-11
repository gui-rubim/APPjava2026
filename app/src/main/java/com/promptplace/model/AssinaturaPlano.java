package com.promptplace.model;

/** HERANÇA + POLIMORFISMO: assinatura do plano Pro ou Equipes. A receita é toda da plataforma. */
public class AssinaturaPlano extends Transacao {

    private final Plano plano;
    private final Cobranca cobranca;

    public AssinaturaPlano(Plano plano, Cobranca cobranca) {
        super(plano.getPrecoTotal(cobranca));
        this.plano = plano;
        this.cobranca = cobranca;
    }

    @Override
    public TipoTransacao getTipo() {
        return TipoTransacao.ASSINATURA;
    }

    @Override
    public String getItem() {
        return Idioma.t(plano.getNome() + " plan (" + cobranca.getNome() + ")",
                "Plano " + plano.getNome() + " (" + cobranca.getNome() + ")");
    }

    @Override
    public double getTaxaPlataforma() {
        return 1.0;
    }
}
