package com.promptplace.model;

/** As 3 verticais da proposta. */
public enum Categoria {
    IMAGEM(new Texto("Image", "Imagem"), "Midjourney, DALL-E, Stable Diffusion"),
    TEXTO(new Texto("Text", "Texto"), "ChatGPT, Gemini, Claude"),
    CODIGO(new Texto("Code", "Código"), "Claude Code, Copilot, Cursor");

    private final Texto nome;
    private final String ferramentas;

    Categoria(Texto nome, String ferramentas) {
        this.nome = nome;
        this.ferramentas = ferramentas;
    }

    public String getNome() {
        return nome.get();
    }

    /** "Image AI" / "IA de Imagem" */
    public String getNomeCompleto() {
        return Idioma.t(getNome() + " AI", "IA de " + getNome());
    }

    public String getFerramentas() {
        return ferramentas;
    }
}
