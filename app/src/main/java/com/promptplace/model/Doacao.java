package com.promptplace.model;

/** HERANÇA + POLIMORFISMO: doação avulsa a um criador. Taxa reduzida de 5%. */
public class Doacao extends Transacao {

    public static final double TAXA = 0.05;
    public static final double MINIMO = 1;

    private final Criador criador; // ASSOCIAÇÃO

    public Doacao(Criador criador, double valor) {
        super(valor);
        if (valor < MINIMO) {
            throw new IllegalArgumentException(Idioma.t(
                    "Enter an amount of at least R$ 1.", "Informe um valor de pelo menos R$ 1."));
        }
        this.criador = criador;
    }

    @Override
    public TipoTransacao getTipo() {
        return TipoTransacao.DOACAO;
    }

    @Override
    public String getItem() {
        return Idioma.t("Support for ", "Apoio a ") + criador.getNome();
    }

    @Override
    public double getTaxaPlataforma() {
        return TAXA;
    }
}
