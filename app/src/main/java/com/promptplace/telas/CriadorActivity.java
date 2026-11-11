package com.promptplace.telas;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.DadosApp;
import com.promptplace.R;
import com.promptplace.model.Categoria;
import com.promptplace.model.Compra;
import com.promptplace.model.Criador;
import com.promptplace.model.Formato;
import com.promptplace.model.Ordenacao;
import com.promptplace.model.Prompt;

import java.util.ArrayList;
import java.util.List;

/** Perfil do criador: vitrine, números, prompts publicados e formulário para publicar. */
public class CriadorActivity extends TelaBase {

    private Categoria categoriaEscolhida = Categoria.MARKETING;
    private LinearLayout chipsCategoria;
    private EditText campoTitulo;
    private EditText campoPreco;
    private EditText campoDescricao;
    private EditText campoConteudo;

    @Override
    protected int itemNav() {
        return R.id.nav_criar;
    }

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_criador);
        chipsCategoria = findViewById(R.id.chips_categoria);
        campoTitulo = findViewById(R.id.edt_titulo);
        campoPreco = findViewById(R.id.edt_preco);
        campoDescricao = findViewById(R.id.edt_descricao);
        campoConteudo = findViewById(R.id.edt_conteudo);

        montarChipsCategoria();
        campoPreco.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                atualizarGanho();
            }
        });
        atualizarGanho();
        findViewById(R.id.btn_publicar).setOnClickListener(v -> publicar());
    }

    private void montarChipsCategoria() {
        chipsCategoria.removeAllViews();
        for (Categoria categoria : Categoria.values()) {
            TextView chip = novoChip(categoria.getNome(), categoria == categoriaEscolhida);
            chip.setOnClickListener(v -> {
                categoriaEscolhida = categoria;
                montarChipsCategoria();
            });
            chipsCategoria.addView(chip);
        }
    }

    /** "Você recebe R$ 16,92 por venda, depois da taxa de 15% da plataforma." */
    private void atualizarGanho() {
        double preco = Math.max(lerNumero(campoPreco.getText().toString()), 0);
        String valor = Formato.dinheiro(preco * (1 - Compra.TAXA));
        String frase = getString(R.string.voce_recebe, valor);
        SpannableStringBuilder texto = new SpannableStringBuilder(frase.substring(0, frase.indexOf(valor)));
        negrito(texto, valor);
        texto.append(frase.substring(frase.indexOf(valor) + valor.length()));
        ((TextView) findViewById(R.id.txt_voce_recebe)).setText(texto);
    }

    private void publicar() {
        List<String> modelos = new ArrayList<>();
        int[] caixas = {R.id.cb_chatgpt, R.id.cb_claude, R.id.cb_gemini, R.id.cb_midjourney};
        for (int id : caixas) {
            CheckBox caixa = findViewById(id);
            if (caixa.isChecked()) {
                modelos.add(caixa.getText().toString());
            }
        }
        double preco = lerNumero(campoPreco.getText().toString());
        if (preco < 0) {
            aviso(R.string.preco_invalido);
            return;
        }
        try {
            Prompt novo = usuario().getPerfilCriador().publicarPrompt(campoTitulo.getText().toString(),
                    categoriaEscolhida, preco, modelos, campoDescricao.getText().toString(),
                    campoConteudo.getText().toString());
            plataforma().adicionarPrompt(novo);

            // Como no protótipo: o marketplace passa a mostrar os mais recentes primeiro.
            DadosApp.busca = "";
            DadosApp.categoria = null;
            DadosApp.ordem = Ordenacao.RECENTES;

            campoTitulo.setText("");
            campoDescricao.setText("");
            campoConteudo.setText("");
            aviso(R.string.publicado);
            atualizar();
        } catch (IllegalArgumentException erro) {
            aviso(erro.getMessage());
        }
    }

    @Override
    protected void atualizar() {
        Criador eu = usuario().getPerfilCriador();
        colocarAvatar(findViewById(R.id.avatar_voce), avatar(eu, 56), 56);
        ((TextView) findViewById(R.id.txt_nome)).setText(eu.getNome());
        // POLIMORFISMO: exibirPerfil() aqui é o do Criador.
        ((TextView) findViewById(R.id.txt_resumo)).setText(eu.exibirPerfil());

        String nota = eu.getTotalAvaliacoes() == 0 ? getString(R.string.sem_notas) : Formato.nota(eu.getNotaMedia());
        adicionarEstatisticas(findViewById(R.id.estatisticas), 2,
                new String[] {String.valueOf(eu.getPrompts().size()), getString(R.string.est_publicados)},
                new String[] {Formato.numero(eu.getVendas()), getString(R.string.est_vendas)},
                new String[] {nota, getString(R.string.est_nota)},
                new String[] {Formato.dinheiro(eu.getGanhosEstimados()), getString(R.string.est_ganhos)});

        LinearLayout meus = findViewById(R.id.lista_meus);
        meus.removeAllViews();
        List<Prompt> prompts = new ArrayList<>(eu.getPrompts());
        prompts.sort(Ordenacao.RECENTES.getComparador());
        for (Prompt p : prompts) {
            adicionarLinha(meus, null, 0, p.getTitulo(),
                    p.getCategoria().getNome() + ", " + Formato.preco(p.getPreco()),
                    plural(p.getVendas(), R.string.venda_uma, R.string.vendas_varias),
                    v -> abrirPrompt(p));
        }
    }
}
