package com.promptplace.model;

/**
 * ABSTRAÇÃO: Usuario é abstrata. Não existe "um usuário qualquer",
 * só Criador ou Consumidor.
 * HERANÇA: Criador e Consumidor herdam desta classe (extends).
 */
public abstract class Usuario {

    // ENCAPSULAMENTO: o id é private e só pode ser lido pelo getter.
    private static int proximoId = 1;
    private final int id;

    // PROTECTED: as subclasses (Criador e Consumidor) acessam direto.
    protected String nome;
    protected String email;

    public Usuario(String nome, String email) {
        this.id = proximoId++;
        this.nome = nome;
        this.email = email;
    }

    // Método abstrato: cada subclasse monta o resumo do perfil do seu jeito.
    public abstract String exibirPerfil();

    /** "Marina Lopes" vira "ML". */
    public String getIniciais() {
        return iniciaisDe(nome);
    }

    /** Iniciais de qualquer nome: "Paulo M." vira "PM". */
    public static String iniciaisDe(String nome) {
        String[] partes = nome.trim().split("\\s+");
        String iniciais = partes[0].substring(0, 1);
        if (partes.length > 1) {
            iniciais += partes[partes.length - 1].substring(0, 1);
        }
        return iniciais.toUpperCase();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
