package com.promptplace.model;

import java.util.ArrayList;
import java.util.List;

/** Guarda os criadores cadastrados e a receita da plataforma (30%). */
public class Plataforma {

    private static double receita = 0;
    private List<Criador> criadores = new ArrayList<>();

    public static void registrarReceita(double valor) {
        receita += valor;
    }

    public static double getReceita() {
        return receita;
    }

    public void cadastrarCriador(Criador criador) {
        criadores.add(criador);
    }

    public List<Criador> getCriadores() {
        return criadores;
    }

    public List<Criador> buscarPorCategoria(Categoria categoria) {
        List<Criador> resultado = new ArrayList<>();
        for (Criador c : criadores) {
            if (c.getCategoria() == categoria) {
                resultado.add(c);
            }
        }
        return resultado;
    }
}
