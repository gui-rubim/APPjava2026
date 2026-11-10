package com.promptplace;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.promptplace.model.Idioma;

/**
 * Salva o idioma escolhido no celular e aplica nas telas.
 * Inglês é o padrão; português é a segunda opção.
 * Os textos das telas ficam em res/values (inglês) e res/values-pt (português).
 */
public class ConfigIdioma {

    private static final String ARQUIVO = "config";
    private static final String CHAVE = "idioma";

    /** Chamado por cada tela antes de abrir: devolve o "contexto" já no idioma certo. */
    public static Context aplicar(Context base) {
        SharedPreferences prefs = base.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        Idioma idioma = Idioma.porCodigo(prefs.getString(CHAVE, Idioma.INGLES.getCodigo()));
        Idioma.setAtual(idioma);

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.setLocale(idioma.getLocale());
        return base.createConfigurationContext(config);
    }

    public static void salvar(Context contexto, Idioma idioma) {
        contexto.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE)
                .edit().putString(CHAVE, idioma.getCodigo()).apply();
        Idioma.setAtual(idioma);
    }
}
