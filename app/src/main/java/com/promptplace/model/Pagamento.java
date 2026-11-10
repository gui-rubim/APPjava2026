package com.promptplace.model;

/**
 * ABSTRAÇÃO: Pagamento é abstrata. O sistema sabe que todo pagamento
 * é "processado", mas cada forma (Pix, Cartão, Boleto) faz isso do seu jeito.
 */
public abstract class Pagamento {

    public static final double TAXA_PLATAFORMA = 0.30; // 30% plataforma, 70% criador

    protected double valor;

    public Pagamento(double valor) {
        this.valor = valor;
    }

    // Métodos abstratos: as subclasses são obrigadas a implementar.
    /** Processa o pagamento e devolve a mensagem para mostrar na tela. */
    public abstract String processar();

    public abstract String getNome();

    // Métodos concretos: iguais para todas as formas de pagamento.
    public double getParteCriador() {
        return valor * (1 - TAXA_PLATAFORMA);
    }

    public double getPartePlataforma() {
        return valor * TAXA_PLATAFORMA;
    }

    public double getValor() {
        return valor;
    }
}
