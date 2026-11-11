package com.promptplace.model;

/** HERANÇA + POLIMORFISMO: assinatura mensal de um criador (R$ 9). Taxa reduzida de 5%. */
public class AssinaturaCriador extends Transacao {

    public static final double PRECO = 9;
    public static final double TAXA = 0.05;

    private final Criador criador; // ASSOCIAÇÃO

    public AssinaturaCriador(Criador criador) {
        super(PRECO);
        this.criador = criador;
    }

    @Override
    public TipoTransacao getTipo() {
        return TipoTransacao.ASSINATURA;
    }

    @Override
    public String getItem() {
        return Idioma.t("Subscription to " + criador.getNome() + " (monthly)",
                "Assinatura de " + criador.getNome() + " (mensal)");
    }

    @Override
    public double getTaxaPlataforma() {
        return TAXA;
    }
}
