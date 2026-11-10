package com.promptplace.model;

/** POLIMORFISMO: Cartão processa o pagamento do seu jeito. */
public class PagamentoCartao extends Pagamento {

    private String finalCartao;

    public PagamentoCartao(double valor, String finalCartao) {
        super(valor);
        this.finalCartao = finalCartao;
    }

    @Override
    public String processar() {
        return Formato.texto(Idioma.t("Monthly charge of R$ %.2f approved on card ending in %s.",
                "Cobrança mensal de R$ %.2f no cartão final %s aprovada."),
                valor, finalCartao);
    }

    @Override
    public String getNome() {
        return Idioma.t("Card", "Cartão");
    }
}
