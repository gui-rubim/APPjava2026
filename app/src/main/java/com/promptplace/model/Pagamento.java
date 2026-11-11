package com.promptplace.model;

/**
 * ABSTRAÇÃO: Pagamento é abstrata. É a FORMA de pagar (Pix, Cartão ou Boleto),
 * usada para recarregar a carteira. Cada forma processa do seu jeito.
 */
public abstract class Pagamento {

    protected double valor;

    public Pagamento(double valor) {
        this.valor = valor;
    }

    // Métodos abstratos: as subclasses são obrigadas a implementar.
    /** Processa o pagamento e devolve a mensagem para mostrar na tela. */
    public abstract String processar();

    public abstract String getNome();

    public double getValor() {
        return valor;
    }
}
