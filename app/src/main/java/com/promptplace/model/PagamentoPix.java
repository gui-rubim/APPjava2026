package com.promptplace.model;

/** POLIMORFISMO: Pix processa o pagamento do seu jeito. */
public class PagamentoPix extends Pagamento {

    public PagamentoPix(double valor) {
        super(valor);
    }

    @Override
    public String processar() {
        return Idioma.t("Pix approved instantly: ", "Pix aprovado na hora: ") + Formato.dinheiro(valor) + ".";
    }

    @Override
    public String getNome() {
        return "Pix";
    }
}
