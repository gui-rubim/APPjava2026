package com.promptplace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * COMPOSIÇÃO: a Biblioteca é criada dentro do Consumidor e não existe sem ele.
 * Guarda os prompts comprados e os favoritos.
 */
public class Biblioteca {

    private final List<Prompt> comprados = new ArrayList<>();
    private final List<Prompt> favoritos = new ArrayList<>();

    public void adicionar(Prompt prompt) {
        if (!comprados.contains(prompt)) {
            comprados.add(prompt);
        }
    }

    public boolean possui(Prompt prompt) {
        return comprados.contains(prompt);
    }

    /** Devolve true se o prompt ficou favoritado. */
    public boolean alternarFavorito(Prompt prompt) {
        if (favoritos.remove(prompt)) {
            return false;
        }
        favoritos.add(prompt);
        return true;
    }

    public boolean isFavorito(Prompt prompt) {
        return favoritos.contains(prompt);
    }

    /** Mais recentes primeiro. */
    public List<Prompt> getComprados() {
        List<Prompt> lista = new ArrayList<>(comprados);
        Collections.reverse(lista);
        return lista;
    }

    /** Mais recentes primeiro. */
    public List<Prompt> getFavoritos() {
        List<Prompt> lista = new ArrayList<>(favoritos);
        Collections.reverse(lista);
        return lista;
    }
}
