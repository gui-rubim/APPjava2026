package com.promptplace.telas;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.DadosApp;
import com.promptplace.R;
import com.promptplace.model.Categoria;
import com.promptplace.model.Formato;
import com.promptplace.model.Ordenacao;
import com.promptplace.model.Prompt;

import java.util.List;

/** Marketplace: pesquisar, filtrar, ordenar, favoritar e abrir prompts. */
public class MarketplaceActivity extends TelaBase {

    private LinearLayout lista;
    private LinearLayout chips;
    private TextView contador;
    private TextView botaoOrdenar;
    private EditText campoBusca;

    @Override
    protected int itemNav() {
        return R.id.nav_marketplace;
    }

    @Override
    protected boolean topoAzul() {
        return true;
    }

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_marketplace);
        lista = findViewById(R.id.lista);
        chips = findViewById(R.id.chips);
        contador = findViewById(R.id.txt_contador);
        botaoOrdenar = findViewById(R.id.btn_ordenar);
        campoBusca = findViewById(R.id.edt_busca);

        campoBusca.setText(DadosApp.busca);
        campoBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void afterTextChanged(Editable texto) {
                DadosApp.busca = texto.toString();
                atualizarLista();
            }
        });

        botaoOrdenar.setOnClickListener(v -> {
            Ordenacao[] ordens = Ordenacao.values();
            String[] nomes = new String[ordens.length];
            for (int i = 0; i < ordens.length; i++) {
                nomes[i] = ordens[i].getNome();
            }
            escolher(getString(R.string.ordenar_por), nomes, DadosApp.ordem.ordinal(), posicao -> {
                DadosApp.ordem = ordens[posicao];
                atualizar();
            });
        });
    }

    @Override
    protected void atualizar() {
        if (!campoBusca.getText().toString().equals(DadosApp.busca)) {
            campoBusca.setText(DadosApp.busca);
        }
        botaoOrdenar.setText(DadosApp.ordem.getNome());
        montarChips();
        atualizarLista();
    }

    private void montarChips() {
        chips.removeAllViews();
        adicionarChip(getString(R.string.todos), null);
        for (Categoria categoria : Categoria.values()) {
            adicionarChip(categoria.getNome(), categoria);
        }
    }

    private void adicionarChip(String nome, Categoria categoria) {
        TextView chip = novoChip(nome, DadosApp.categoria == categoria);
        chip.setOnClickListener(v -> {
            DadosApp.categoria = categoria;
            montarChips();
            atualizarLista();
        });
        chips.addView(chip);
    }

    private void atualizarLista() {
        List<Prompt> encontrados = plataforma().buscar(DadosApp.busca, DadosApp.categoria, DadosApp.ordem);
        contador.setText(plural(encontrados.size(), R.string.encontrado_um, R.string.encontrado_varios));

        lista.removeAllViews();
        if (encontrados.isEmpty()) {
            TextView vazio = new TextView(this);
            vazio.setText(R.string.nada_encontrado);
            vazio.setBackgroundResource(R.drawable.cartao);
            vazio.setPadding(dp(20), dp(24), dp(20), dp(24));
            vazio.setTextColor(getColor(R.color.suave));
            lista.addView(vazio);
            return;
        }
        for (Prompt prompt : encontrados) {
            lista.addView(cartao(prompt));
        }
    }

    /** O cartão do prompt, igual ao do protótipo. */
    private View cartao(Prompt prompt) {
        View cartao = getLayoutInflater().inflate(R.layout.item_prompt, lista, false);

        LinearLayout etiquetas = cartao.findViewById(R.id.prompt_tags);
        etiquetas.addView(etiqueta(prompt.getCategoria().getNome(), R.drawable.tag, R.color.azul));
        if (usuario().getBiblioteca().possui(prompt)) {
            etiquetas.addView(etiqueta(getString(R.string.na_biblioteca), R.drawable.tag_verde, R.color.menta));
        }

        ImageView coracao = cartao.findViewById(R.id.prompt_coracao);
        pintarCoracao(coracao, usuario().getBiblioteca().isFavorito(prompt));
        coracao.setOnClickListener(v -> pintarCoracao(coracao, usuario().alternarFavorito(prompt)));

        ((TextView) cartao.findViewById(R.id.prompt_titulo)).setText(prompt.getTitulo());
        ((TextView) cartao.findViewById(R.id.prompt_por)).setText(getString(R.string.por, prompt.getCriador().getNome()));
        ((TextView) cartao.findViewById(R.id.prompt_desc)).setText(prompt.getDescricao());

        LinearLayout modelos = cartao.findViewById(R.id.prompt_modelos);
        for (String modelo : prompt.getModelos()) {
            modelos.addView(etiquetaModelo(modelo));
        }

        ((TextView) cartao.findViewById(R.id.prompt_nota)).setText(textoNota(prompt));
        ((TextView) cartao.findViewById(R.id.prompt_preco)).setText(Formato.preco(prompt.getPreco()));
        cartao.setOnClickListener(v -> abrirPrompt(prompt));
        return cartao;
    }
}
