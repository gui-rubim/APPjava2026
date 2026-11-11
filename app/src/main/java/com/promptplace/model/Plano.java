package com.promptplace.model;

/** Planos para quem compra: Gratuito, Pro e Equipes. */
public enum Plano {
    GRATUITO(new Texto("Free", "Gratuito"), 0, new Texto[] {
            new Texto("Search, preview and one-off purchases", "Busca, prévia e compra avulsa"),
            new Texto("Personal library", "Biblioteca pessoal"),
            new Texto("Reviews from verified buyers", "Avaliações de compradores verificados")}),
    PRO(new Texto("Pro", "Pro"), 29, new Texto[] {
            new Texto("Everything in Free", "Tudo do plano Gratuito"),
            new Texto("Premium collection that changes every month", "Coleção premium que muda todo mês"),
            new Texto("5 premium prompts per month", "5 prompts premium por mês"),
            new Texto("10% off one-off purchases", "10% de desconto nas compras avulsas")}),
    EQUIPES(new Texto("Teams", "Equipes"), 99, new Texto[] {
            new Texto("Everything in Pro", "Tudo do plano Pro"),
            new Texto("5 seats", "5 assentos"),
            new Texto("Shared library", "Biblioteca compartilhada"),
            new Texto("Centralized billing", "Faturamento centralizado")});

    private final Texto nome;
    private final double precoMensal;
    private final Texto[] recursos;

    Plano(Texto nome, double precoMensal, Texto[] recursos) {
        this.nome = nome;
        this.precoMensal = precoMensal;
        this.recursos = recursos;
    }

    /** Quanto sai por mês (no anual, já com 20% de desconto). */
    public double getPrecoPorMes(Cobranca cobranca) {
        return cobranca == Cobranca.ANUAL ? precoMensal * (1 - Cobranca.DESCONTO_ANUAL) : precoMensal;
    }

    /** Quanto é cobrado de uma vez: 1 mês ou 12 meses com desconto. */
    public double getPrecoTotal(Cobranca cobranca) {
        if (cobranca == Cobranca.ANUAL) {
            return Math.round(precoMensal * 12 * (1 - Cobranca.DESCONTO_ANUAL) * 100) / 100.0;
        }
        return precoMensal;
    }

    public String getNome() {
        return nome.get();
    }

    public double getPrecoMensal() {
        return precoMensal;
    }

    public String[] getRecursos() {
        String[] lista = new String[recursos.length];
        for (int i = 0; i < recursos.length; i++) {
            lista[i] = recursos[i].get();
        }
        return lista;
    }
}
