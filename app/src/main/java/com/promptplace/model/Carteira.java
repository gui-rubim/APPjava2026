package com.promptplace.model;

/**
 * COMPOSIÇÃO: a Carteira pertence a um Consumidor e só existe dentro dele.
 * ENCAPSULAMENTO: o saldo é private; só muda por debitar() e recarregar().
 */
public class Carteira {

    private double saldo;

    public Carteira(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    /** Tira dinheiro da carteira. Se não tiver saldo, lança SaldoInsuficienteException. */
    public void debitar(double valor) {
        if (saldo + 0.0001 < valor) {
            throw new SaldoInsuficienteException();
        }
        saldo = Math.round((saldo - valor) * 100) / 100.0;
    }

    /** POLIMORFISMO: processar() é o do Pix, do Cartão ou do Boleto. */
    public String recarregar(Pagamento forma) {
        String mensagem = forma.processar();
        saldo += forma.getValor();
        return mensagem;
    }

    public double getSaldo() {
        return saldo;
    }
}
