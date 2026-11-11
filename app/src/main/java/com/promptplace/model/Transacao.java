package com.promptplace.model;

import java.util.Date;

/**
 * ABSTRAÇÃO: toda movimentação de dinheiro é uma Transacao, mas cada tipo
 * (Compra, Assinatura, Doação, Comissão) tem a sua própria taxa e descrição.
 * POLIMORFISMO: getTipo(), getItem() e getTaxaPlataforma() mudam em cada subclasse.
 */
public abstract class Transacao {

    protected final double valor;
    protected Situacao situacao = Situacao.CONCLUIDO;
    private Date data = new Date();

    public Transacao(double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException(Idioma.t(
                    "The amount can't be negative.", "O valor não pode ser negativo."));
        }
        this.valor = Math.round(valor * 100) / 100.0;
    }

    public abstract TipoTransacao getTipo();

    /** O que aparece na coluna "Item" do histórico. */
    public abstract String getItem();

    /** Quanto (de 0 a 1) fica com a plataforma. */
    public abstract double getTaxaPlataforma();

    public double getReceitaPlataforma() {
        return valor * getTaxaPlataforma();
    }

    public double getRepasseCriador() {
        return valor - getReceitaPlataforma();
    }

    /** Só para os dados de demonstração: transação feita alguns dias atrás. */
    public void definirDiasAtras(int dias) {
        data = new Date(System.currentTimeMillis() - dias * 86_400_000L);
    }

    public double getValor() {
        return valor;
    }

    public Date getData() {
        return data;
    }

    public Situacao getSituacao() {
        return situacao;
    }
}
