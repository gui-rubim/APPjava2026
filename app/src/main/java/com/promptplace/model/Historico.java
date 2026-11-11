package com.promptplace.model;

import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSIÇÃO: o Histórico pertence ao Consumidor.
 * Guarda todas as transações (compras, assinaturas, doações e comissões).
 */
public class Historico {

    private final List<Transacao> transacoes = new ArrayList<>();

    public void registrar(Transacao transacao) {
        transacoes.add(0, transacao); // a mais recente fica em cima
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public double getTotal() {
        double total = 0;
        for (Transacao t : transacoes) {
            total += t.getValor();
        }
        return total;
    }

    /** Monta o extrato em CSV (abre no Excel e no Google Planilhas). */
    public String gerarCsv() {
        StringBuilder csv = new StringBuilder("﻿");
        csv.append(linha(Idioma.t("Date", "Data"), Idioma.t("Type", "Tipo"), "Item",
                Idioma.t("Amount (R$)", "Valor (R$)"), Idioma.t("Status", "Situação")));
        for (Transacao t : transacoes) {
            csv.append(linha(Formato.data(t.getData()), t.getTipo().getNome(), t.getItem(),
                    Formato.texto("%.2f", t.getValor()), t.getSituacao().getNome()));
        }
        return csv.toString();
    }

    private static String linha(String... colunas) {
        StringBuilder linha = new StringBuilder();
        for (int i = 0; i < colunas.length; i++) {
            if (i > 0) {
                linha.append(';');
            }
            linha.append('"').append(colunas[i].replace("\"", "\"\"")).append('"');
        }
        return linha.append("\r\n").toString();
    }
}
