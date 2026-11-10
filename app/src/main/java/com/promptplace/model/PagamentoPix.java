package com.promptplace.model;

/** POLIMORFISMO: Pix processa o pagamento do seu jeito. */
public class PagamentoPix extends Pagamento {

    public PagamentoPix(double valor) {
        super(valor);
    }

    @Override
    public String processar() {
        return Formato.texto(Idioma.t("Pix QR code generated. R$ %.2f approved instantly!",
                "QR Code Pix gerado. R$ %.2f aprovado na hora!"), valor);
    }

    @Override
    public String getNome() {
        return "Pix";
    }
}
