package com.promptplace.telas;

import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.MaskFilterSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.Avaliacao;
import com.promptplace.model.Formato;
import com.promptplace.model.Prompt;

import java.util.List;

/**
 * Detalhe do prompt (o "modal" do protótipo): prévia parcial com o resto borrado,
 * comprar, copiar, favoritar e as avaliações recentes.
 */
public class PromptActivity extends TelaBase {

    private Prompt prompt;

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_prompt);
        prompt = promptRecebido();
        if (prompt == null) {
            finish();
            return;
        }
        setTitle(prompt.getCategoria().getNome());

        findViewById(R.id.btn_favoritar).setOnClickListener(v -> {
            usuario().alternarFavorito(prompt);
            atualizar();
        });
        findViewById(R.id.btn_avaliar).setOnClickListener(v -> {
            Intent intent = new Intent(this, ComunidadeActivity.class);
            intent.putExtra(EXTRA_PROMPT, prompt.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }

    @Override
    protected void atualizar() {
        boolean possui = usuario().getBiblioteca().possui(prompt);
        boolean favorito = usuario().getBiblioteca().isFavorito(prompt);

        LinearLayout etiquetas = findViewById(R.id.linha_tags);
        etiquetas.removeAllViews();
        etiquetas.addView(etiqueta(prompt.getCategoria().getNome(), R.drawable.tag, R.color.azul));
        for (String modelo : prompt.getModelos()) {
            etiquetas.addView(etiquetaModelo(modelo));
        }

        ((TextView) findViewById(R.id.txt_titulo)).setText(prompt.getTitulo());
        colocarAvatar(findViewById(R.id.avatar_criador), avatar(prompt.getCriador(), 28), 28);
        SpannableStringBuilder por = new SpannableStringBuilder(getString(R.string.por_curto) + " ");
        negrito(por, prompt.getCriador().getNome());
        ((TextView) findViewById(R.id.txt_por)).setText(por);
        ((TextView) findViewById(R.id.txt_desc)).setText(prompt.getDescricao());
        ((TextView) findViewById(R.id.txt_nota)).setText(textoNota(prompt));
        ((TextView) findViewById(R.id.txt_vendas)).setText(
                plural(prompt.getVendas(), R.string.venda_uma, R.string.vendas_varias));

        // Prompt: completo para quem comprou; prévia + resto borrado para quem não comprou.
        TextView conteudo = findViewById(R.id.txt_conteudo);
        if (possui) {
            conteudo.setLayerType(View.LAYER_TYPE_NONE, null);
            conteudo.setText(destacarVariaveis(prompt.getConteudo()));
            conteudo.setTextIsSelectable(true);
        } else {
            SpannableStringBuilder texto = destacarVariaveis(prompt.getPrevia());
            int inicio = texto.length();
            texto.append(prompt.getParteBloqueada());
            texto.setSpan(new MaskFilterSpan(new BlurMaskFilter(dp(4), BlurMaskFilter.Blur.NORMAL)),
                    inicio, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            conteudo.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // o borrão precisa disso
            conteudo.setTextIsSelectable(false);
            conteudo.setText(texto);
        }
        ((TextView) findViewById(R.id.txt_aviso)).setText(possui ? R.string.prompt_e_seu : R.string.previa_parcial);

        Button principal = findViewById(R.id.btn_principal);
        if (possui) {
            principal.setText(R.string.copiar_prompt);
            principal.setOnClickListener(v -> copiar(prompt));
        } else {
            principal.setText(prompt.isGratis()
                    ? getString(R.string.adicionar_gratis)
                    : getString(R.string.comprar_por, Formato.dinheiro(prompt.getPreco())));
            principal.setOnClickListener(v -> comprar(prompt));
        }
        findViewById(R.id.btn_avaliar).setVisibility(possui ? View.VISIBLE : View.GONE);

        Button botaoFavorito = findViewById(R.id.btn_favoritar);
        // Sem o botão "Avaliar" ao lado, o "Favoritar" ocupa a linha inteira.
        LinearLayout.LayoutParams margemFavorito = (LinearLayout.LayoutParams) botaoFavorito.getLayoutParams();
        margemFavorito.setMarginStart(possui ? dp(5) : 0);
        botaoFavorito.setLayoutParams(margemFavorito);
        botaoFavorito.setText(favorito ? R.string.nos_favoritos : R.string.favoritar);
        botaoFavorito.setTextColor(getColor(favorito ? R.color.coral : R.color.tinta));

        // Avaliações recentes (até 2)
        LinearLayout lista = findViewById(R.id.lista_avaliacoes);
        lista.removeAllViews();
        List<Avaliacao> avaliacoes = plataforma().getAvaliacoes(prompt);
        findViewById(R.id.txt_secao_avaliacoes).setVisibility(avaliacoes.isEmpty() ? View.GONE : View.VISIBLE);
        for (int i = 0; i < Math.min(2, avaliacoes.size()); i++) {
            Avaliacao a = avaliacoes.get(i);
            SpannableStringBuilder titulo = new SpannableStringBuilder(estrelas(a.getEstrelas()));
            titulo.append("  ");
            negrito(titulo, a.getNomeAutor());
            TextView cartao = new TextView(this);
            cartao.setBackgroundResource(R.drawable.cartao);
            cartao.setPadding(dp(16), dp(14), dp(16), dp(14));
            cartao.setTextColor(getColor(R.color.tinta));
            cartao.setTextSize(14);
            cartao.setLineSpacing(dp(2), 1);
            titulo.append("\n").append(a.getTexto());
            cartao.setText(titulo);
            LinearLayout.LayoutParams margem = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            margem.bottomMargin = dp(10);
            lista.addView(cartao, margem);
        }
    }
}
