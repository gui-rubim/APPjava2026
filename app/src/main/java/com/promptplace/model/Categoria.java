package com.promptplace.model;

/** Categorias do marketplace. */
public enum Categoria {
    MARKETING(new Texto("Marketing", "Marketing")),
    PROGRAMACAO(new Texto("Programming", "Programação")),
    ESTUDOS(new Texto("Studies", "Estudos")),
    DESIGN(new Texto("Design", "Design")),
    NEGOCIOS(new Texto("Business", "Negócios"));

    private final Texto nome;

    Categoria(Texto nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome.get();
    }
}
