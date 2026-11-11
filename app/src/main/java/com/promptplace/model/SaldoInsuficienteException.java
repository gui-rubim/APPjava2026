package com.promptplace.model;

/** Exceção própria do app: lançada quando a carteira não tem dinheiro suficiente. */
public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException() {
        super(Idioma.t(
                "Insufficient balance. Tap \"+ R$ 100\" to top up.",
                "Saldo insuficiente. Toque em \"+ R$ 100\" para recarregar."));
    }
}
