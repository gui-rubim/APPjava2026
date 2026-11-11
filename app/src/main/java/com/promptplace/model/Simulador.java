package com.promptplace.model;

/** Simulador de receita do modelo de negócio (considera só a taxa sobre vendas). */
public class Simulador {

    private final double volume;

    public Simulador(int vendasPorMes, double ticketMedio) {
        this.volume = vendasPorMes * ticketMedio;
    }

    /** Total movimentado no mês. */
    public double getVolume() {
        return volume;
    }

    /** O que vai para os criadores (85%). */
    public double getRepasseCriadores() {
        return volume * (1 - Compra.TAXA);
    }

    /** O que fica com a plataforma (15%). */
    public double getReceitaPlataforma() {
        return volume * Compra.TAXA;
    }
}
