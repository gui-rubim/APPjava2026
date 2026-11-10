package com.promptplace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** HERANÇA: Consumidor é um Usuario (extends). */
public class Consumidor extends Usuario {

    // COMPOSIÇÃO: a biblioteca nasce junto com o consumidor.
    private final Biblioteca biblioteca;
    private List<Assinatura> assinaturas = new ArrayList<>();
    private List<Criador> testeCegoUsado = new ArrayList<>();
    private Random sorteio = new Random();

    public Consumidor(String nome, String email) {
        super(nome, email);
        this.biblioteca = new Biblioteca(); // criada aqui, pertence só a ele
    }

    /**
     * Teste cego: 1 prompt grátis e aleatório por criador.
     * Retorna o prompt sorteado ou null se a chance já foi usada.
     */
    public Prompt fazerTesteCego(Criador criador) {
        if (jaUsouTesteCego(criador)) {
            return null;
        }
        List<Prompt> cofre = criador.getCofre();
        Prompt sorteado = cofre.get(sorteio.nextInt(cofre.size()));
        biblioteca.adicionar(sorteado);
        testeCegoUsado.add(criador);
        return sorteado;
    }

    public boolean jaUsouTesteCego(Criador criador) {
        return testeCegoUsado.contains(criador);
    }

    /** Assinar um criador: paga, libera o cofre inteiro e devolve o recibo. */
    public String assinar(Criador criador, Pagamento pagamento) {
        Assinatura assinatura = new Assinatura(this, criador, pagamento);
        String recibo = assinatura.ativar();
        assinaturas.add(assinatura);
        for (Prompt p : criador.getCofre()) {
            biblioteca.adicionar(p);
        }
        return recibo;
    }

    public boolean assina(Criador criador) {
        for (Assinatura a : assinaturas) {
            if (a.getCriador() == criador) {
                return true;
            }
        }
        return false;
    }

    // POLIMORFISMO: outra implementação do mesmo método de Usuario.
    @Override
    public String exibirPerfil() {
        int qtd = assinaturas.size();
        return qtd + (qtd == 1 ? Idioma.t(" subscription", " assinatura")
                               : Idioma.t(" subscriptions", " assinaturas"))
                + "  ·  " + biblioteca.getPrompts().size()
                + Idioma.t(" prompts in library", " prompts na biblioteca");
    }

    public Biblioteca getBiblioteca() {
        return biblioteca;
    }

    public List<Assinatura> getAssinaturas() {
        return assinaturas;
    }
}
