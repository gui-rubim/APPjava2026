package com.promptplace.model;

/** POLIMORFISMO: Cartão processa o pagamento do seu jeito. */
public class PagamentoCartao extends Pagamento {

    private final String finalCartao;

    public PagamentoCartao(double valor, String finalCartao) {
        super(valor);
        this.finalCartao = finalCartao;
    }

    @Override
    public String processar() {
        return Idioma.t("Card ending in " + finalCartao + " charged: ",
                "Cartão final " + finalCartao + " cobrado: ") + Formato.dinheiro(valor) + ".";
    }

    @Override
    public String getNome() {
        return Idioma.t("Credit card", "Cartão de crédito");
    }
}
