package com.promptplace.model;

import java.util.Comparator;

/** Formas de ordenar o marketplace. Cada uma carrega o seu próprio comparador. */
public enum Ordenacao {
    RELEVANTES(new Texto("Most relevant", "Mais relevantes"),
            (a, b) -> Double.compare(b.getRelevancia(), a.getRelevancia())),
    MELHOR_AVALIADOS(new Texto("Top rated", "Melhor avaliados"),
            (a, b) -> a.getNota() != b.getNota()
                    ? Double.compare(b.getNota(), a.getNota())
                    : Integer.compare(b.getQuantidadeAvaliacoes(), a.getQuantidadeAvaliacoes())),
    MAIS_VENDIDOS(new Texto("Best selling", "Mais vendidos"),
            (a, b) -> Integer.compare(b.getVendas(), a.getVendas())),
    RECENTES(new Texto("Newest", "Mais recentes"),
            (a, b) -> Integer.compare(b.getId(), a.getId())),
    MENOR_PRECO(new Texto("Lowest price", "Menor preço"),
            (a, b) -> Double.compare(a.getPreco(), b.getPreco())),
    MAIOR_PRECO(new Texto("Highest price", "Maior preço"),
            (a, b) -> Double.compare(b.getPreco(), a.getPreco()));

    private final Texto nome;
    private final Comparator<Prompt> comparador;

    Ordenacao(Texto nome, Comparator<Prompt> comparador) {
        this.nome = nome;
        this.comparador = comparador;
    }

    public String getNome() {
        return nome.get();
    }

    public Comparator<Prompt> getComparador() {
        return comparador;
    }
}
