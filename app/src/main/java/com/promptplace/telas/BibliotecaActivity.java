package com.promptplace.telas;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.Prompt;

import java.util.List;

/** Biblioteca pessoal: abas Comprados e Favoritos. */
public class BibliotecaActivity extends TelaBase {

    private boolean abaFavoritos = false;
    private LinearLayout lista;
    private TextView abaComprados;
    private TextView abaFavoritosBotao;

    @Override
    protected int itemNav() {
        return R.id.nav_biblioteca;
    }

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_biblioteca);
        lista = findViewById(R.id.lista);
        abaComprados = findViewById(R.id.aba_comprados);
        abaFavoritosBotao = findViewById(R.id.aba_favoritos);
        abaComprados.setOnClickListener(v -> {
            abaFavoritos = false;
            atualizar();
        });
        abaFavoritosBotao.setOnClickListener(v -> {
            abaFavoritos = true;
            atualizar();
        });
    }

    @Override
    protected void atualizar() {
        List<Prompt> comprados = usuario().getBiblioteca().getComprados();
        List<Prompt> favoritos = usuario().getBiblioteca().getFavoritos();
        abaComprados.setText(getString(R.string.aba_comprados, comprados.size()));
        abaFavoritosBotao.setText(getString(R.string.aba_favoritos, favoritos.size()));
        marcarChip(abaComprados, !abaFavoritos);
        marcarChip(abaFavoritosBotao, abaFavoritos);

        List<Prompt> itens = abaFavoritos ? favoritos : comprados;
        lista.removeAllViews();
        if (itens.isEmpty()) {
            TextView vazio = new TextView(this);
            vazio.setText(abaFavoritos ? R.string.sem_favoritos : R.string.sem_compras);
            vazio.setTextColor(getColor(R.color.suave));
            vazio.setPadding(dp(18), dp(20), dp(18), dp(20));
            lista.addView(vazio);
            return;
        }
        for (Prompt prompt : itens) {
            lista.addView(linha(prompt));
        }
    }

    private View linha(Prompt prompt) {
        boolean possui = usuario().getBiblioteca().possui(prompt);
        View linha = getLayoutInflater().inflate(R.layout.item_biblioteca, lista, false);

        TextView titulo = linha.findViewById(R.id.bib_titulo);
        titulo.setText(prompt.getTitulo());
        titulo.setOnClickListener(v -> abrirPrompt(prompt));
        ((TextView) linha.findViewById(R.id.bib_info)).setText(
                getString(R.string.por_categoria, prompt.getCriador().getNome(), prompt.getCategoria().getNome()));
        ((TextView) linha.findViewById(R.id.bib_linha)).setText(
                possui ? prompt.getPrimeiraLinha() : getString(R.string.compre_para_liberar));

        linha.findViewById(R.id.bib_abrir).setOnClickListener(v -> abrirPrompt(prompt));
        Button acao = linha.findViewById(R.id.bib_acao);
        if (possui) {
            acao.setText(R.string.copiar);
            acao.setOnClickListener(v -> copiar(prompt));
        } else {
            acao.setText(prompt.isGratis() ? R.string.adicionar : R.string.comprar);
            acao.setOnClickListener(v -> comprar(prompt));
        }

        ImageView coracao = linha.findViewById(R.id.bib_coracao);
        coracao.setVisibility(abaFavoritos ? View.VISIBLE : View.GONE);
        coracao.setOnClickListener(v -> {
            usuario().alternarFavorito(prompt);
            atualizar();
        });
        return linha;
    }
}
