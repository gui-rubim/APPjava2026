package com.promptplace.model;

/** HERANÇA + POLIMORFISMO: compra de um prompt no marketplace. A plataforma fica com 15%. */
public class Compra extends Transacao {

    public static final double TAXA = 0.15;

    private final Prompt prompt; // ASSOCIAÇÃO

    public Compra(Prompt prompt) {
        super(prompt.getPreco());
        this.prompt = prompt;
    }

    @Override
    public TipoTransacao getTipo() {
        return TipoTransacao.COMPRA;
    }

    @Override
    public String getItem() {
        return prompt.getTitulo();
    }

    @Override
    public double getTaxaPlataforma() {
        return TAXA;
    }
}
