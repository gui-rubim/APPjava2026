package com.promptplace.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Formata números, valores e datas no padrão do idioma atual. */
public class Formato {

    public static String texto(String modelo, Object... valores) {
        return String.format(Idioma.getAtual().getLocale(), modelo, valores);
    }

    /** R$ 2.987,33 (português) ou R$ 2,987.33 (inglês). */
    public static String dinheiro(double valor) {
        return texto("R$ %,.2f", valor);
    }

    /** Igual a dinheiro(), mas mostra "Grátis" quando o valor é zero. */
    public static String preco(double valor) {
        return valor == 0 ? Idioma.t("Free", "Grátis") : dinheiro(valor);
    }

    public static String numero(long numero) {
        return texto("%,d", numero);
    }

    public static String nota(double nota) {
        return texto("%.1f", nota);
    }

    public static String data(Date data) {
        String padrao = Idioma.getAtual() == Idioma.PORTUGUES ? "dd/MM/yyyy" : "MM/dd/yyyy";
        return new SimpleDateFormat(padrao, Idioma.getAtual().getLocale()).format(data);
    }
}
