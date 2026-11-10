package com.promptplace.model;

/** ASSOCIAÇÃO: liga um Consumidor a uma nota dada a um Prompt. */
public class Avaliacao {

    private Consumidor autor;
    private int nota;
    private Texto comentario;

    /** Avaliação escrita pelo usuário (o comentário fica igual nos dois idiomas). */
    public Avaliacao(Consumidor autor, int nota, String comentario) {
        this(autor, nota, new Texto(comentario));
    }

    /** Avaliação de exemplo, com o comentário nos dois idiomas. */
    public Avaliacao(Consumidor autor, int nota, Texto comentario) {
        // ENCAPSULAMENTO: a regra fica protegida dentro da classe.
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException(Idioma.t(
                    "Pick a rating from 1 to 5 stars.", "Escolha uma nota de 1 a 5 estrelas."));
        }
        this.autor = autor;
        this.nota = nota;
        this.comentario = comentario;
    }

    public int getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario.get();
    }

    public Consumidor getAutor() {
        return autor;
    }

}
