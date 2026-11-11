package com.promptplace.telas;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.Avaliacao;
import com.promptplace.model.Criador;
import com.promptplace.model.Formato;
import com.promptplace.model.Prompt;

import java.util.List;

/** Avaliações e comunidade: avaliar, ranking de criadores e mural de avaliações. */
public class ComunidadeActivity extends TelaBase {

    private Prompt escolhido;
    private TextView botaoEscolher;
    private RatingBar estrelasNota;
    private EditText campoComentario;

    @Override
    protected int itemNav() {
        return R.id.nav_comunidade;
    }

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_comunidade);
        botaoEscolher = findViewById(R.id.btn_escolher_prompt);
        estrelasNota = findViewById(R.id.rating);
        campoComentario = findViewById(R.id.edt_comentario);

        botaoEscolher.setOnClickListener(v -> {
            List<Prompt> comprados = usuario().getBiblioteca().getComprados();
            if (comprados.isEmpty()) {
                aviso(R.string.compre_para_avaliar);
                return;
            }
            String[] titulos = new String[comprados.size()];
            for (int i = 0; i < titulos.length; i++) {
                titulos[i] = comprados.get(i).getTitulo();
            }
            escolher(getString(R.string.campo_prompt_avaliar), titulos, comprados.indexOf(escolhido), posicao -> {
                escolhido = comprados.get(posicao);
                botaoEscolher.setText(escolhido.getTitulo());
            });
        });
        findViewById(R.id.btn_publicar_avaliacao).setOnClickListener(v -> publicar());

        // O que a comunidade faz (lista fixa, igual ao protótipo)
        LinearLayout faz = findViewById(R.id.lista_faz);
        int[] itens = {R.string.faz_1, R.string.faz_2, R.string.faz_3, R.string.faz_4};
        for (int item : itens) {
            adicionarLinha(faz, icone(R.drawable.ic_check_simples), 34, getString(item), null, null, null);
        }
    }

    /** Chega aqui pelo botão "Avaliar este prompt": já deixa o prompt escolhido. */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void publicar() {
        if (escolhido == null) {
            aviso(R.string.compre_para_avaliar);
            return;
        }
        try {
            Avaliacao avaliacao = usuario().avaliar(escolhido, (int) estrelasNota.getRating(),
                    campoComentario.getText().toString());
            plataforma().registrarAvaliacao(avaliacao);
            estrelasNota.setRating(0);
            campoComentario.setText("");
            aviso(R.string.avaliacao_publicada);
            atualizar();
        } catch (IllegalArgumentException erro) {
            aviso(erro.getMessage());
        }
    }

    @Override
    protected void atualizar() {
        // Prompt que veio da tela de detalhe (botão "Avaliar este prompt")
        Prompt pedido = promptRecebido();
        if (pedido != null) {
            escolhido = pedido;
            getIntent().removeExtra(EXTRA_PROMPT);
            ((ScrollView) findViewById(R.id.rolagem)).scrollTo(0, 0);
            campoComentario.requestFocus();
        }
        List<Prompt> comprados = usuario().getBiblioteca().getComprados();
        if (escolhido == null || !comprados.contains(escolhido)) {
            escolhido = comprados.isEmpty() ? null : comprados.get(0);
        }
        botaoEscolher.setText(escolhido == null ? getString(R.string.compre_para_avaliar) : escolhido.getTitulo());

        // Ranking de criadores por vendas
        LinearLayout ranking = findViewById(R.id.lista_ranking);
        ranking.removeAllViews();
        List<Criador> criadores = plataforma().getRanking();
        for (int i = 0; i < criadores.size(); i++) {
            Criador c = criadores.get(i);
            boolean souEu = c == usuario().getPerfilCriador();
            String nota = c.getTotalAvaliacoes() == 0
                    ? getString(R.string.sem_notas)
                    : getString(R.string.nota_x, Formato.nota(c.getNotaMedia()));
            adicionarLinha(ranking, avatar(c, 40), 40,
                    (i + 1) + ".  " + c.getNome() + (souEu ? " " + getString(R.string.voce_marcador) : ""),
                    nota, plural(c.getVendas(), R.string.venda_uma, R.string.vendas_varias), null);
        }

        // Mural de avaliações
        LinearLayout mural = findViewById(R.id.lista_avaliacoes);
        mural.removeAllViews();
        for (Avaliacao a : plataforma().getAvaliacoes()) {
            mural.addView(cartao(a));
        }
    }

    private View cartao(Avaliacao a) {
        View cartao = getLayoutInflater().inflate(R.layout.item_avaliacao, null, false);
        colocarAvatar(cartao.findViewById(R.id.aval_avatar), avatarIniciais(a.getIniciaisAutor(), 40), 40);
        ((TextView) cartao.findViewById(R.id.aval_nome)).setText(a.getNomeAutor());
        ((TextView) cartao.findViewById(R.id.aval_data)).setText(Formato.data(a.getData()));
        ((TextView) cartao.findViewById(R.id.aval_estrelas)).setText(estrelas(a.getEstrelas()));
        cartao.findViewById(R.id.aval_verificada).setVisibility(a.isVerificada() ? View.VISIBLE : View.GONE);

        TextView prompt = cartao.findViewById(R.id.aval_prompt);
        prompt.setText(a.getPrompt().getTitulo());
        prompt.setOnClickListener(v -> abrirPrompt(a.getPrompt()));
        ((TextView) cartao.findViewById(R.id.aval_texto)).setText(a.getTexto());

        TextView util = cartao.findViewById(R.id.aval_util);
        util.setText(getString(R.string.util, a.getUteis()));
        marcarChip(util, usuario().marcouComoUtil(a));
        util.setOnClickListener(v -> {
            marcarChip(util, usuario().alternarUtil(a));
            util.setText(getString(R.string.util, a.getUteis()));
        });

        LinearLayout.LayoutParams margem = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        margem.bottomMargin = dp(12);
        cartao.setLayoutParams(margem);
        return cartao;
    }
}
