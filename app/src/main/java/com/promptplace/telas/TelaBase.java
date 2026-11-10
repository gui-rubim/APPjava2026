package com.promptplace.telas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.promptplace.ConfigIdioma;
import com.promptplace.DadosApp;
import com.promptplace.R;
import com.promptplace.model.Consumidor;
import com.promptplace.model.Criador;
import com.promptplace.model.Prompt;

/**
 * HERANÇA e ABSTRAÇÃO também nas telas: todas as telas (menos o login)
 * herdam desta classe, que cuida do que é comum a elas.
 */
public abstract class TelaBase extends Activity {

    /** Cada tela monta o seu layout aqui. */
    protected abstract void montarTela();

    /** Chamado sempre que a tela aparece, para mostrar os dados atualizados. */
    protected void atualizar() {
    }

    /** Qual item da barra de navegação fica aceso (0 = tela sem barra). */
    protected int itemNav() {
        return 0;
    }

    /** Abre a tela já no idioma escolhido (inglês ou português). */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ConfigIdioma.aplicar(base));
    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se o Android fechou o app em segundo plano, os dados somem: volta ao login.
        if (DadosApp.getUsuario() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        montarTela();
        if (!isFinishing()) {
            configurarBarras();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFinishing()) {
            atualizar();
        }
    }

    /** O título da tela aparece na barra superior. */
    @Override
    protected void onTitleChanged(CharSequence titulo, int cor) {
        super.onTitleChanged(titulo, cor);
        TextView campo = findViewById(R.id.txt_topo_titulo);
        if (campo != null) {
            campo.setText(titulo);
        }
    }

    private void configurarBarras() {
        View voltar = findViewById(R.id.btn_voltar);
        if (voltar != null) {
            voltar.setOnClickListener(v -> finish());
        }

        if (findViewById(R.id.barra_nav) == null) {
            return;
        }
        int[] itens = {R.id.nav_inicio, R.id.nav_biblioteca, R.id.nav_perfil};
        Class<?>[] telas = {FeedActivity.class, BibliotecaActivity.class, PerfilActivity.class};
        for (int i = 0; i < itens.length; i++) {
            LinearLayout item = findViewById(itens[i]);
            boolean ativo = itens[i] == itemNav();
            int cor = getColor(ativo ? R.color.azul_claro : R.color.texto_3);
            item.setBackgroundResource(ativo ? R.drawable.nav_ativo : 0);
            ((ImageView) item.getChildAt(0)).setImageTintList(ColorStateList.valueOf(cor));
            ((TextView) item.getChildAt(1)).setTextColor(cor);

            Class<?> destino = telas[i];
            item.setOnClickListener(v -> {
                if (!ativo) {
                    Intent intent = new Intent(this, destino);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }
    }

    // ------------------------------------------------------------------
    // Funções de apoio usadas pelas telas
    // ------------------------------------------------------------------

    protected Consumidor usuario() {
        return DadosApp.getUsuario();
    }

    /** Foto (quadrada) do criador. */
    protected int fotoDe(Criador criador) {
        return getResources().getIdentifier(criador.getFoto(), "drawable", getPackageName());
    }

    /** Foto de capa (larga) do criador. */
    protected int capaDe(Criador criador) {
        return getResources().getIdentifier(criador.getFoto() + "_capa", "drawable", getPackageName());
    }

    /** Corta a view no formato do fundo dela (círculo ou cantos arredondados). */
    protected void recortar(View view) {
        view.setClipToOutline(true);
    }

    /** Linha de lista com a foto redonda de um criador. */
    protected View adicionarLinhaFoto(LinearLayout lista, Criador criador, String titulo,
                                      String sub, String valor, View.OnClickListener clique) {
        View linha = criarLinha(lista, titulo, sub, valor, clique);
        ImageView imagem = linha.findViewById(R.id.linha_imagem);
        imagem.setImageResource(fotoDe(criador));
        imagem.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imagem.setBackgroundResource(R.drawable.circulo);
        recortar(imagem);
        return linha;
    }

    /** Linha de lista com um ícone num quadradinho. */
    protected View adicionarLinhaIcone(LinearLayout lista, int icone, int cor, String titulo,
                                       String sub, String valor, View.OnClickListener clique) {
        View linha = criarLinha(lista, titulo, sub, valor, clique);
        ImageView imagem = linha.findViewById(R.id.linha_imagem);
        imagem.setImageResource(icone);
        imagem.setImageTintList(ColorStateList.valueOf(getColor(cor)));
        imagem.setBackgroundResource(R.drawable.icone_fundo);
        int espaco = (int) (11 * getResources().getDisplayMetrics().density);
        imagem.setPadding(espaco, espaco, espaco, espaco);
        return linha;
    }

    private View criarLinha(LinearLayout lista, String titulo, String sub, String valor,
                            View.OnClickListener clique) {
        View linha = getLayoutInflater().inflate(R.layout.item_linha, lista, false);
        preencher(linha.findViewById(R.id.linha_titulo), titulo);
        preencher(linha.findViewById(R.id.linha_sub), sub);
        preencher(linha.findViewById(R.id.linha_valor), valor);
        if (clique != null) {
            linha.setOnClickListener(clique);
        } else {
            linha.findViewById(R.id.linha_seta).setVisibility(View.GONE);
            linha.setBackground(null);
        }
        lista.addView(linha);
        return linha;
    }

    /** Coloca uma estrelinha antes do texto do valor da linha. */
    protected void comEstrela(View linha) {
        TextView valor = linha.findViewById(R.id.linha_valor);
        valor.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_estrela_pequena, 0, 0, 0);
    }

    private void preencher(TextView campo, String texto) {
        if (texto == null || texto.isEmpty()) {
            campo.setVisibility(View.GONE);
        } else {
            campo.setText(texto);
        }
    }

    protected void ativarBotao(View botao, boolean ativo) {
        botao.setEnabled(ativo);
        botao.setAlpha(ativo ? 1f : 0.45f);
    }

    /** Escolhe o texto no singular ou plural: "1 review" / "3 reviews". */
    protected String plural(int quantidade, int textoUm, int textoVarios) {
        return getString(quantidade == 1 ? textoUm : textoVarios, quantidade);
    }

    protected void aviso(int texto) {
        aviso(getString(texto));
    }

    protected void aviso(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    // As telas trocam informações pelo número (posição) do criador e do prompt.
    protected static final String EXTRA_CRIADOR = "criador";
    protected static final String EXTRA_PROMPT = "prompt";

    protected Criador criadorRecebido() {
        int posicao = getIntent().getIntExtra(EXTRA_CRIADOR, 0);
        return DadosApp.getPlataforma().getCriadores().get(posicao);
    }

    protected Prompt promptRecebido() {
        int posicao = getIntent().getIntExtra(EXTRA_PROMPT, 0);
        return criadorRecebido().getCofre().get(posicao);
    }

    protected void abrirCriador(Criador criador) {
        Intent intent = new Intent(this, CriadorActivity.class);
        intent.putExtra(EXTRA_CRIADOR, DadosApp.getPlataforma().getCriadores().indexOf(criador));
        startActivity(intent);
    }

    protected void abrirPrompt(Prompt prompt) {
        Criador criador = prompt.getCriador();
        Intent intent = new Intent(this, PromptActivity.class);
        intent.putExtra(EXTRA_CRIADOR, DadosApp.getPlataforma().getCriadores().indexOf(criador));
        intent.putExtra(EXTRA_PROMPT, criador.getCofre().indexOf(prompt));
        startActivity(intent);
    }

    protected void abrirBiblioteca() {
        Intent intent = new Intent(this, BibliotecaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
