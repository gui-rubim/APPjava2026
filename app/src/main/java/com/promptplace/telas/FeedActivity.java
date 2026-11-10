package com.promptplace.telas;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.DadosApp;
import com.promptplace.R;
import com.promptplace.model.Categoria;
import com.promptplace.model.Criador;
import com.promptplace.model.Formato;

import java.util.List;

/** Início: resumo da biblioteca e lista de criadores com filtro por categoria. */
public class FeedActivity extends TelaBase {

    private static final Categoria[] CATEGORIAS = {null, Categoria.IMAGEM, Categoria.TEXTO, Categoria.CODIGO};

    private Categoria filtro = null; // null = todas
    private LinearLayout lista;
    private TextView[] segmentos;

    @Override
    protected int itemNav() {
        return R.id.nav_inicio;
    }

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_feed);
        lista = findViewById(R.id.lista);

        ((TextView) findViewById(R.id.txt_iniciais)).setText(usuario().getIniciais());
        ((TextView) findViewById(R.id.txt_ola)).setText(getString(R.string.ola, usuario().getNome()));
        findViewById(R.id.btn_ver_biblioteca).setOnClickListener(v -> abrirBiblioteca());

        segmentos = new TextView[] {
                findViewById(R.id.btn_todos),
                findViewById(R.id.btn_imagem),
                findViewById(R.id.btn_texto),
                findViewById(R.id.btn_codigo)
        };
        for (int i = 0; i < segmentos.length; i++) {
            Categoria categoria = CATEGORIAS[i];
            segmentos[i].setOnClickListener(v -> {
                filtro = categoria;
                atualizar();
            });
        }
    }

    @Override
    protected void atualizar() {
        // Cartão de destaque
        List<Criador> todos = DadosApp.getPlataforma().getCriadores();
        int testesLivres = 0;
        for (Criador c : todos) {
            if (!usuario().assina(c) && !usuario().jaUsouTesteCego(c)) {
                testesLivres++;
            }
        }
        int assinaturas = usuario().getAssinaturas().size();
        ((TextView) findViewById(R.id.txt_total_prompts)).setText(
                String.valueOf(usuario().getBiblioteca().getPrompts().size()));
        ((TextView) findViewById(R.id.txt_resumo)).setText(
                plural(assinaturas, R.string.assinatura_um, R.string.assinatura_varios) + "  ·  "
                        + plural(testesLivres, R.string.teste_livre_um, R.string.teste_livre_varios));

        // Filtro selecionado
        for (int i = 0; i < segmentos.length; i++) {
            boolean ativo = CATEGORIAS[i] == filtro;
            segmentos[i].setBackgroundResource(ativo ? R.drawable.segmento_ativo : 0);
            segmentos[i].setTextColor(getColor(ativo ? R.color.branco : R.color.texto_2));
        }

        // Cartões dos criadores
        List<Criador> criadores = filtro == null ? todos : DadosApp.getPlataforma().buscarPorCategoria(filtro);
        lista.removeAllViews();
        for (Criador c : criadores) {
            adicionarCartaoCriador(c);
        }
    }

    private void adicionarCartaoCriador(Criador c) {
        View cartao = getLayoutInflater().inflate(R.layout.item_criador, lista, false);
        recortar(cartao);

        ((ImageView) cartao.findViewById(R.id.criador_capa)).setImageResource(capaDe(c));
        ImageView foto = cartao.findViewById(R.id.criador_foto);
        foto.setImageResource(fotoDe(c));
        recortar(foto);

        ((TextView) cartao.findViewById(R.id.criador_nome)).setText(c.getNome());
        ((TextView) cartao.findViewById(R.id.criador_categoria)).setText(c.getCategoria().getNomeCompleto());
        ((TextView) cartao.findViewById(R.id.criador_preco)).setText(c.getPrecoFormatado());

        String nota = c.getTotalAvaliacoes() == 0
                ? getString(R.string.novo)
                : Formato.texto("%.1f", c.getNotaMedia());
        ((TextView) cartao.findViewById(R.id.criador_info)).setText(
                nota + "  ·  " + c.getCofre().size() + " " + getString(R.string.prompts));

        cartao.findViewById(R.id.criador_selo).setVisibility(usuario().assina(c) ? View.VISIBLE : View.GONE);
        cartao.setOnClickListener(v -> abrirCriador(c));
        lista.addView(cartao);
    }
}
