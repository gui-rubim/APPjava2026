package com.promptplace.model;

/** POLIMORFISMO: Boleto processa o pagamento do seu jeito. */
public class PagamentoBoleto extends Pagamento {

    public PagamentoBoleto(double valor) {
        super(valor);
    }

    @Override
    public String processar() {
        return Idioma.t("Boleto issued (in this demo it counts as paid): ",
                "Boleto emitido (na demonstração, já conta como pago): ") + Formato.dinheiro(valor) + ".";
    }

    @Override
    public String getNome() {
        return "Boleto";
    }
}
