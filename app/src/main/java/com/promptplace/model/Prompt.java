package com.promptplace.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ASSOCIAÇÃO: um Prompt conhece o Criador que o escreveu
 * e a lista de Avaliacoes que recebeu.
 */
public class Prompt {

    private Texto titulo;
    private Texto conteudo;
    private Categoria categoria;
    private Criador criador;                                // associação
    private List<Avaliacao> avaliacoes = new ArrayList<>(); // associação

    public Prompt(Texto titulo, Texto conteudo, Categoria categoria, Criador criador) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.categoria = categoria;
        this.criador = criador;
    }

    public void adicionarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(avaliacao);
    }

    public double getMediaNotas() {
        if (avaliacoes.isEmpty()) {
            return 0;
        }
        double soma = 0;
        for (Avaliacao a : avaliacoes) {
            soma += a.getNota();
        }
        return soma / avaliacoes.size();
    }

    public String getTextoNota() {
        if (avaliacoes.isEmpty()) {
            return Idioma.t("New", "Novo");
        }
        return Formato.texto("%.1f", getMediaNotas());
    }

    public String getTitulo() {
        return titulo.get();
    }

    /** O texto que o usuário copia (mecânica "Copy & Paste"). */
    public String getConteudo() {
        return conteudo.get();
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Criador getCriador() {
        return criador;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }
}
