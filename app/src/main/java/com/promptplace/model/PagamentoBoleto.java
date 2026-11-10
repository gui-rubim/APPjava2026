package com.promptplace.model;

/** POLIMORFISMO: Boleto processa o pagamento do seu jeito. */
public class PagamentoBoleto extends Pagamento {

    public PagamentoBoleto(double valor) {
        super(valor);
    }

    @Override
    public String processar() {
        return Formato.texto(Idioma.t("Boleto for R$ %.2f issued. (In this demo it counts as paid.)",
                "Boleto de R$ %.2f emitido. (Na demonstração, já consideramos pago.)"), valor);
    }

    @Override
    public String getNome() {
        return "Boleto";
    }
}
