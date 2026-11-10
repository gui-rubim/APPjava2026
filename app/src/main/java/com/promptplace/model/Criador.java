package com.promptplace.model;

import java.util.ArrayList;
import java.util.List;

/** HERANÇA: Criador é um Usuario (extends). */
public class Criador extends Usuario {

    public static final double PRECO_MINIMO = 10.00;

    // ENCAPSULAMENTO: atributos private, acessados por métodos public.
    private double precoAssinatura;
    private double saldo;
    private Texto bio;
    private String foto;
    private Categoria categoria;
    private List<Prompt> cofre = new ArrayList<>(); // ASSOCIAÇÃO com Prompt

    public Criador(String nome, String email, Texto bio, String foto,
                   Categoria categoria, double precoAssinatura) {
        super(nome, email); // chama o construtor de Usuario
        this.bio = bio;
        this.foto = foto;
        this.categoria = categoria;
        setPrecoAssinatura(precoAssinatura);
    }

    /** Regra da proposta: preço mínimo de R$ 10,00. */
    public void setPrecoAssinatura(double preco) {
        if (preco < PRECO_MINIMO) {
            throw new IllegalArgumentException(Idioma.t(
                    "The minimum subscription price is R$ 10.00.",
                    "O preço mínimo da assinatura é R$ 10,00."));
        }
        this.precoAssinatura = preco;
    }

    public void publicarPrompt(Texto titulo, Texto conteudo) {
        cofre.add(new Prompt(titulo, conteudo, categoria, this));
    }

    /** Recebe a parte do criador (70%) de cada assinatura. */
    public void receber(double valor) {
        saldo += valor;
    }

    // POLIMORFISMO: implementação própria do método abstrato de Usuario.
    @Override
    public String exibirPerfil() {
        return categoria.getNomeCompleto() + "  ·  " + cofre.size()
                + Idioma.t(" prompts in the vault", " prompts no cofre");
    }

    /** Média das notas de todos os prompts do cofre (0 se ninguém avaliou). */
    public double getNotaMedia() {
        double soma = 0;
        int quantidade = 0;
        for (Prompt p : cofre) {
            for (Avaliacao a : p.getAvaliacoes()) {
                soma += a.getNota();
                quantidade++;
            }
        }
        return quantidade == 0 ? 0 : soma / quantidade;
    }

    public int getTotalAvaliacoes() {
        int total = 0;
        for (Prompt p : cofre) {
            total += p.getAvaliacoes().size();
        }
        return total;
    }

    public String getPrecoFormatado() {
        return Formato.texto("R$ %.2f", precoAssinatura);
    }

    /** Nome da imagem da foto do criador (ex.: "foto_marina"). */
    public String getFoto() {
        return foto;
    }

    public double getPrecoAssinatura() {
        return precoAssinatura;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getBio() {
        return bio.get();
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public List<Prompt> getCofre() {
        return cofre;
    }
}
