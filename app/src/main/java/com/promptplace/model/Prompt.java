package com.promptplace.model;

import java.util.List;

/**
 * ASSOCIAÇÃO: um Prompt conhece o Criador que o publicou.
 * Guarda preço, nota, vendas e o texto completo (que só aparece inteiro para quem comprou).
 */
public class Prompt {

    private static int proximoId = 1;

    private final int id;
    private Texto titulo;
    private Texto descricao;
    private Texto conteudo;
    private Categoria categoria;
    private List<String> modelos; // ferramentas de IA em que funciona
    private double preco;
    private Criador criador;       // associação
    private double nota;
    private int quantidadeAvaliacoes;
    private int vendas;

    public Prompt(Texto titulo, Texto descricao, Texto conteudo, Categoria categoria,
                  List<String> modelos, double preco, Criador criador) {
        if (preco < 0) {
            throw new IllegalArgumentException(Idioma.t(
                    "The price can't be negative.", "O preço não pode ser negativo."));
        }
        this.id = proximoId++;
        this.titulo = titulo;
        this.descricao = descricao;
        this.conteudo = conteudo;
        this.categoria = categoria;
        this.modelos = modelos;
        this.preco = preco;
        this.criador = criador;
        criador.adicionar(this);
    }

    /** Recomeça a numeração (usado quando a demonstração é reiniciada). */
    public static void reiniciarNumeracao() {
        proximoId = 1;
    }

    /** Números de exemplo para a demonstração (nota, avaliações e vendas). */
    public void definirEstatisticas(double nota, int avaliacoes, int vendas) {
        this.nota = nota;
        this.quantidadeAvaliacoes = avaliacoes;
        this.vendas = vendas;
    }

    public void registrarVenda() {
        vendas++;
    }

    /** Recalcula a média quando chega uma avaliação nova. */
    public void registrarNota(int estrelas) {
        nota = (nota * quantidadeAvaliacoes + estrelas) / (quantidadeAvaliacoes + 1);
        quantidadeAvaliacoes++;
    }

    public boolean isGratis() {
        return preco == 0;
    }

    /** Usado para ordenar por "mais relevantes": nota alta e muitas vendas. */
    public double getRelevancia() {
        return nota * Math.log(2 + vendas);
    }

    /** Onde a prévia termina: cerca de 35% do texto, no máximo 150 letras. */
    private int pontoDeCorte() {
        String texto = getConteudo();
        int corte = Math.min((int) Math.ceil(texto.length() * 0.35), 150);
        int espaco = texto.lastIndexOf(' ', corte);
        return espaco > 40 ? espaco : corte;
    }

    /** Parte do prompt que qualquer pessoa pode ver. */
    public String getPrevia() {
        return getConteudo().substring(0, pontoDeCorte());
    }

    /** Parte que só aparece (sem borrão) depois da compra. */
    public String getParteBloqueada() {
        return getConteudo().substring(pontoDeCorte());
    }

    public String getPrimeiraLinha() {
        return getConteudo().split("\n")[0];
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo.get();
    }

    public String getDescricao() {
        return descricao.get();
    }

    /** O texto completo, que o comprador copia (mecânica "Copy & Paste"). */
    public String getConteudo() {
        return conteudo.get();
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public List<String> getModelos() {
        return modelos;
    }

    public double getPreco() {
        return preco;
    }

    public Criador getCriador() {
        return criador;
    }

    public double getNota() {
        return nota;
    }

    public int getQuantidadeAvaliacoes() {
        return quantidadeAvaliacoes;
    }

    public int getVendas() {
        return vendas;
    }
}
