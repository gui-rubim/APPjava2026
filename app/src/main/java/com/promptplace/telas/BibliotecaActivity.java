package com.promptplace.telas;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.Prompt;

import java.util.List;

/** Biblioteca pessoal: todos os prompts que o usuário desbloqueou. */
public class BibliotecaActivity extends TelaBase {

    private LinearLayout lista;

    @Override
    protected int itemNav() {
        return R.id.nav_biblioteca;
    }

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_biblioteca);
        lista = findViewById(R.id.lista);
    }

    @Override
    protected void atualizar() {
        List<Prompt> prompts = usuario().getBiblioteca().getPrompts();
        boolean vazia = prompts.isEmpty();

        ((TextView) findViewById(R.id.txt_subtitulo)).setText(vazia
                ? getString(R.string.biblioteca_vazia)
                : plural(prompts.size(), R.string.prontos_um, R.string.prontos_varios));
        findViewById(R.id.vazio).setVisibility(vazia ? View.VISIBLE : View.GONE);
        lista.setVisibility(vazia ? View.GONE : View.VISIBLE);

        lista.removeAllViews();
        for (Prompt p : prompts) {
            View linha = adicionarLinhaFoto(lista, p.getCriador(), p.getTitulo(),
                    p.getCriador().getNome() + "  ·  " + p.getCategoria().getNome(),
                    p.getTextoNota(), v -> abrirPrompt(p));
            if (!p.getAvaliacoes().isEmpty()) {
                comEstrela(linha);
            }
        }
    }
}
