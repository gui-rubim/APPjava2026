package com.promptplace.model;

import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSIÇÃO: a Biblioteca é criada dentro do Consumidor
 * e não existe sem ele (ver o construtor de Consumidor).
 */
public class Biblioteca {

    private List<Prompt> prompts = new ArrayList<>();

    public void adicionar(Prompt prompt) {
        if (!prompts.contains(prompt)) {
            prompts.add(prompt);
        }
    }

    public boolean contem(Prompt prompt) {
        return prompts.contains(prompt);
    }

    public List<Prompt> getPrompts() {
        return prompts;
    }
}
