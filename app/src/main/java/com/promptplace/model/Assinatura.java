package com.promptplace.model;

/** ASSOCIAÇÃO: liga Consumidor + Criador + Pagamento (é a "compra"). */
public class Assinatura {

    private Consumidor consumidor;
    private Criador criador;
    private Pagamento pagamento;

    public Assinatura(Consumidor consumidor, Criador criador, Pagamento pagamento) {
        this.consumidor = consumidor;
        this.criador = criador;
        this.pagamento = pagamento;
    }

    /** Processa o pagamento, divide o valor e devolve o recibo. */
    public String ativar() {
        // POLIMORFISMO: chama processar() sem saber se é Pix, Cartão ou Boleto.
        String mensagem = pagamento.processar();
        criador.receber(pagamento.getParteCriador());
        Plataforma.registrarReceita(pagamento.getPartePlataforma());
        return mensagem + Formato.texto(Idioma.t(
                "%n%nHow it's split:%n• R$ %.2f to %s (70%%)%n• R$ %.2f to the platform (30%%)",
                "%n%nDivisão do valor:%n• R$ %.2f para %s (70%%)%n• R$ %.2f para a plataforma (30%%)"),
                pagamento.getParteCriador(), criador.getNome(), pagamento.getPartePlataforma());
    }

    public Consumidor getConsumidor() {
        return consumidor;
    }

    public Criador getCriador() {
        return criador;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }
}
