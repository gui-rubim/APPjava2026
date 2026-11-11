package com.promptplace.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** HERANÇA: Consumidor é um Usuario (extends). É quem compra, assina, doa e encomenda. */
public class Consumidor extends Usuario {

    public static final double SALDO_INICIAL = 250;
    public static final double VALOR_RECARGA = 100;

    // COMPOSIÇÃO: carteira, biblioteca e histórico nascem junto com o consumidor
    // e não existem sem ele.
    private final Carteira carteira = new Carteira(SALDO_INICIAL);
    private final Biblioteca biblioteca = new Biblioteca();
    private final Historico historico = new Historico();

    private Criador perfilCriador; // ASSOCIAÇÃO: o perfil de criador da mesma pessoa
    private Plano plano = Plano.GRATUITO;
    private Cobranca cobranca = Cobranca.MENSAL;
    private final List<Encomenda> encomendas = new ArrayList<>();
    private final Set<Avaliacao> marcadasComoUteis = new HashSet<>();

    public Consumidor(String nome, String email) {
        super(nome, email);
    }

    /**
     * POLIMORFISMO: recebe qualquer tipo de Transacao (Compra, Doacao, Comissao...).
     * Tira o valor da carteira e guarda no histórico.
     */
    private void pagar(Transacao transacao) {
        carteira.debitar(transacao.getValor());
        historico.registrar(transacao);
    }

    public void comprar(Prompt prompt) {
        if (biblioteca.possui(prompt)) {
            return;
        }
        pagar(new Compra(prompt));
        biblioteca.adicionar(prompt);
        prompt.registrarVenda();
    }

    /** Liga ou desliga o favorito. Devolve true se ficou favoritado. */
    public boolean alternarFavorito(Prompt prompt) {
        return biblioteca.alternarFavorito(prompt);
    }

    public void assinarPlano(Plano novoPlano, Cobranca novaCobranca) {
        if (novoPlano.getPrecoTotal(novaCobranca) > 0) {
            pagar(new AssinaturaPlano(novoPlano, novaCobranca));
        }
        plano = novoPlano;
        cobranca = novaCobranca;
    }

    public boolean isPlanoAtual(Plano outro, Cobranca outraCobranca) {
        return plano == outro && (outro == Plano.GRATUITO || cobranca == outraCobranca);
    }

    public void doar(Criador criador, double valor) {
        pagar(new Doacao(criador, valor));
    }

    public void assinarCriador(Criador criador) {
        pagar(new AssinaturaCriador(criador));
    }

    public Encomenda encomendar(Criador criador, String briefing, double orcamento, int prazoDias) {
        Encomenda encomenda = new Encomenda(criador, briefing, orcamento, prazoDias);
        pagar(encomenda.getComissao());
        encomendas.add(0, encomenda);
        return encomenda;
    }

    /** Só quem comprou pode avaliar. */
    public Avaliacao avaliar(Prompt prompt, int estrelas, String texto) {
        if (!biblioteca.possui(prompt)) {
            throw new IllegalArgumentException(Idioma.t(
                    "Buy a prompt to be able to review it.", "Compre um prompt para poder avaliá-lo."));
        }
        Avaliacao avaliacao = new Avaliacao(this, prompt, estrelas, texto);
        prompt.registrarNota(estrelas);
        return avaliacao;
    }

    /** Marca ou desmarca uma avaliação como útil. Devolve true se ficou marcada. */
    public boolean alternarUtil(Avaliacao avaliacao) {
        if (marcadasComoUteis.remove(avaliacao)) {
            avaliacao.alterarUteis(-1);
            return false;
        }
        marcadasComoUteis.add(avaliacao);
        avaliacao.alterarUteis(1);
        return true;
    }

    public boolean marcouComoUtil(Avaliacao avaliacao) {
        return marcadasComoUteis.contains(avaliacao);
    }

    /** POLIMORFISMO: a recarga aceita qualquer forma de Pagamento (Pix, Cartão ou Boleto). */
    public String recarregar(Pagamento forma) {
        return carteira.recarregar(forma);
    }

    /** Só para montar a demonstração: uma compra feita antes de abrir o app. */
    public void carregarCompraAnterior(Prompt prompt, int diasAtras) {
        Compra compra = new Compra(prompt);
        compra.definirDiasAtras(diasAtras);
        historico.registrar(compra);
        biblioteca.adicionar(prompt);
    }

    // POLIMORFISMO: outra implementação do mesmo método de Usuario.
    @Override
    public String exibirPerfil() {
        return Idioma.t("Plan ", "Plano ") + plano.getNome() + "  ·  "
                + biblioteca.getComprados().size() + Idioma.t(" prompts in library", " prompts na biblioteca");
    }

    public void setPerfilCriador(Criador perfilCriador) {
        this.perfilCriador = perfilCriador;
    }

    public Criador getPerfilCriador() {
        return perfilCriador;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    public Biblioteca getBiblioteca() {
        return biblioteca;
    }

    public Historico getHistorico() {
        return historico;
    }

    public Plano getPlano() {
        return plano;
    }

    public List<Encomenda> getEncomendas() {
        return encomendas;
    }
}
