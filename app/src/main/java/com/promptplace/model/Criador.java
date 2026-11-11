package com.promptplace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** HERANÇA: Criador é um Usuario (extends). Publica prompts e recebe pelas vendas. */
public class Criador extends Usuario {

    // ENCAPSULAMENTO: atributos private, acessados por métodos public.
    private String handle;
    private String foto; // nome da imagem; null = mostra as iniciais
    private List<Prompt> prompts = new ArrayList<>(); // ASSOCIAÇÃO com Prompt

    public Criador(String nome, String email, String handle, String foto) {
        super(nome, email); // chama o construtor de Usuario
        this.handle = handle;
        this.foto = foto;
    }

    /** Publica um prompt novo. As regras de validação ficam aqui dentro. */
    public Prompt publicarPrompt(String titulo, Categoria categoria, double preco,
                                 List<String> modelos, String descricao, String conteudo) {
        if (titulo.trim().isEmpty() || descricao.trim().isEmpty() || conteudo.trim().isEmpty()) {
            throw new IllegalArgumentException(Idioma.t(
                    "Fill in the title, the short description and the full prompt.",
                    "Preencha o título, a descrição curta e o prompt completo."));
        }
        List<String> funcionaEm = modelos.isEmpty()
                ? new ArrayList<>(Collections.singletonList("ChatGPT")) : modelos;
        return new Prompt(new Texto(titulo.trim()), new Texto(descricao.trim()),
                new Texto(conteudo.trim()), categoria, funcionaEm,
                Math.round(preco * 100) / 100.0, this);
    }

    /** Chamado pelo construtor de Prompt: o prompt passa a fazer parte do perfil. */
    void adicionar(Prompt prompt) {
        prompts.add(prompt);
    }

    public int getVendas() {
        int total = 0;
        for (Prompt p : prompts) {
            total += p.getVendas();
        }
        return total;
    }

    public int getTotalAvaliacoes() {
        int total = 0;
        for (Prompt p : prompts) {
            total += p.getQuantidadeAvaliacoes();
        }
        return total;
    }

    /** Média das notas, com mais peso para os prompts com mais avaliações. */
    public double getNotaMedia() {
        int total = getTotalAvaliacoes();
        if (total == 0) {
            return 0;
        }
        double soma = 0;
        for (Prompt p : prompts) {
            soma += p.getNota() * p.getQuantidadeAvaliacoes();
        }
        return soma / total;
    }

    /** Quanto o criador ganhou: vendas x preço, já descontada a taxa da plataforma. */
    public double getGanhosEstimados() {
        double total = 0;
        for (Prompt p : prompts) {
            total += p.getVendas() * p.getPreco() * (1 - Compra.TAXA);
        }
        return total;
    }

    // POLIMORFISMO: implementação própria do método abstrato de Usuario.
    @Override
    public String exibirPerfil() {
        // "nome" é protected em Usuario: a subclasse acessa direto.
        return handle + "  ·  " + prompts.size() + Idioma.t(" prompts published", " prompts publicados");
    }

    public String getHandle() {
        return handle;
    }

    public String getFoto() {
        return foto;
    }

    public List<Prompt> getPrompts() {
        return prompts;
    }
}
