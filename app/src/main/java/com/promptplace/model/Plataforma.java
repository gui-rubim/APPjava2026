package com.promptplace.model;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/** Guarda os criadores, os prompts do marketplace e o mural de avaliações. */
public class Plataforma {

    private final List<Criador> criadores = new ArrayList<>();
    private final List<Prompt> prompts = new ArrayList<>();
    private final List<Avaliacao> avaliacoes = new ArrayList<>();

    public void cadastrarCriador(Criador criador) {
        criadores.add(criador);
    }

    public void adicionarPrompt(Prompt prompt) {
        prompts.add(prompt);
    }

    public void registrarAvaliacao(Avaliacao avaliacao) {
        avaliacoes.add(0, avaliacao); // a mais recente fica em cima
    }

    /** Busca por título, criador, categoria, descrição ou ferramenta; filtra e ordena. */
    public List<Prompt> buscar(String busca, Categoria categoria, Ordenacao ordem) {
        String procurado = normalizar(busca.trim());
        List<Prompt> resultado = new ArrayList<>();
        for (Prompt p : prompts) {
            if (categoria != null && p.getCategoria() != categoria) {
                continue;
            }
            String ondeProcurar = normalizar(p.getTitulo() + " " + p.getCriador().getNome() + " "
                    + p.getCategoria().getNome() + " " + p.getDescricao() + " " + String.join(" ", p.getModelos()));
            if (procurado.isEmpty() || ondeProcurar.contains(procurado)) {
                resultado.add(p);
            }
        }
        resultado.sort(ordem.getComparador());
        return resultado;
    }

    /** Tira acentos e deixa minúsculo: "Programação" vira "programacao". */
    private static String normalizar(String texto) {
        return Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    public List<Avaliacao> getAvaliacoes(Prompt prompt) {
        List<Avaliacao> lista = new ArrayList<>();
        for (Avaliacao a : avaliacoes) {
            if (a.getPrompt() == prompt) {
                lista.add(a);
            }
        }
        return lista;
    }

    /** Criadores ordenados por número de vendas. */
    public List<Criador> getRanking() {
        List<Criador> ranking = new ArrayList<>(criadores);
        ranking.sort((a, b) -> Integer.compare(b.getVendas(), a.getVendas()));
        return ranking;
    }

    public Prompt getPrompt(int id) {
        for (Prompt p : prompts) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Criador> getCriadores() {
        return criadores;
    }

    public List<Prompt> getPrompts() {
        return prompts;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }
}
