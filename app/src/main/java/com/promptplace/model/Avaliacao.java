package com.promptplace.model;

import java.util.Date;

/** ASSOCIAÇÃO: liga quem avaliou a um Prompt, com nota, comentário e votos de "útil". */
public class Avaliacao {

    private Consumidor autor;   // quem está usando o app (null nas avaliações de exemplo)
    private String nomeAutor;   // nome dos usuários de exemplo
    private Prompt prompt;
    private int estrelas;
    private Texto texto;
    private int uteis;
    private Date data;

    /** Avaliação de exemplo, já com o texto nos dois idiomas. */
    public Avaliacao(String nomeAutor, Prompt prompt, int estrelas, Texto texto, int uteis, int diasAtras) {
        validar(estrelas, texto.get());
        this.nomeAutor = nomeAutor;
        this.prompt = prompt;
        this.estrelas = estrelas;
        this.texto = texto;
        this.uteis = uteis;
        this.data = new Date(System.currentTimeMillis() - diasAtras * 86_400_000L);
    }

    /** Avaliação feita por quem usa o app. */
    public Avaliacao(Consumidor autor, Prompt prompt, int estrelas, String texto) {
        validar(estrelas, texto);
        this.autor = autor;
        this.prompt = prompt;
        this.estrelas = estrelas;
        this.texto = new Texto(texto.trim());
        this.data = new Date();
    }

    // ENCAPSULAMENTO: as regras ficam protegidas dentro da classe.
    private static void validar(int estrelas, String texto) {
        if (estrelas < 1 || estrelas > 5) {
            throw new IllegalArgumentException(Idioma.t("Choose from 1 to 5 stars.", "Escolha de 1 a 5 estrelas."));
        }
        if (texto.trim().length() < 5) {
            throw new IllegalArgumentException(Idioma.t("Write a short comment.", "Escreva um comentário curto."));
        }
    }

    /** Só o Consumidor (mesmo pacote) mexe nos votos de "útil". */
    void alterarUteis(int diferenca) {
        uteis += diferenca;
    }

    public String getNomeAutor() {
        return autor != null ? Idioma.t("You", "Você") : nomeAutor;
    }

    public String getIniciaisAutor() {
        return autor != null ? autor.getIniciais() : Usuario.iniciaisDe(nomeAutor);
    }

    /** Só quem comprou consegue avaliar, então toda avaliação é de compra verificada. */
    public boolean isVerificada() {
        return true;
    }

    public Prompt getPrompt() {
        return prompt;
    }

    public int getEstrelas() {
        return estrelas;
    }

    public String getTexto() {
        return texto.get();
    }

    public int getUteis() {
        return uteis;
    }

    public Date getData() {
        return data;
    }
}
