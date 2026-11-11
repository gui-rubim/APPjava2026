package com.promptplace.telas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.promptplace.ConfigIdioma;
import com.promptplace.DadosApp;
import com.promptplace.R;
import com.promptplace.model.Consumidor;
import com.promptplace.model.Criador;
import com.promptplace.model.Formato;
import com.promptplace.model.Pagamento;
import com.promptplace.model.PagamentoBoleto;
import com.promptplace.model.PagamentoCartao;
import com.promptplace.model.PagamentoPix;
import com.promptplace.model.Plataforma;
import com.promptplace.model.Prompt;
import com.promptplace.model.SaldoInsuficienteException;
import com.promptplace.model.Usuario;

import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HERANÇA e ABSTRAÇÃO também nas telas: todas as telas (menos o login)
 * herdam desta classe, que cuida do que é comum a elas.
 */
public abstract class TelaBase extends Activity {

    protected static final String EXTRA_PROMPT = "prompt";

    /** Cada tela monta o seu layout aqui. */
    protected abstract void montarTela();

    /** Chamado sempre que a tela aparece, para mostrar os dados atualizados. */
    protected void atualizar() {
    }

    /** Qual item da barra de navegação fica aceso (0 = tela sem barra). */
    protected int itemNav() {
        return 0;
    }

    /** Telas com o topo azul pintam a barra de status de azul. */
    protected boolean topoAzul() {
        return false;
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
            pintarBarrasDoSistema(this, topoAzul());
            configurarBarras();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFinishing()) {
            atualizarCarteira();
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

    /** Barra de status azul (telas com topo azul) ou clara com ícones escuros. */
    static void pintarBarrasDoSistema(Activity tela, boolean azul) {
        Window janela = tela.getWindow();
        janela.setStatusBarColor(tela.getColor(azul ? R.color.azul : R.color.papel));
        int flags = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        if (!azul) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        janela.getDecorView().setSystemUiVisibility(flags);
    }

    private void configurarBarras() {
        View voltar = findViewById(R.id.btn_voltar);
        if (voltar != null) {
            voltar.setOnClickListener(v -> finish());
        }

        View recarga = findViewById(R.id.carteira_mais);
        if (recarga != null) {
            recarga.setOnClickListener(v -> escolherFormaDeRecarga());
        }

        if (findViewById(R.id.barra_nav) == null) {
            return;
        }
        int[] itens = {R.id.nav_marketplace, R.id.nav_biblioteca, R.id.nav_criar, R.id.nav_comunidade, R.id.nav_mais};
        Class<?>[] telas = {MarketplaceActivity.class, BibliotecaActivity.class, CriadorActivity.class,
                ComunidadeActivity.class, MaisActivity.class};
        for (int i = 0; i < itens.length; i++) {
            LinearLayout item = findViewById(itens[i]);
            boolean ativo = itens[i] == itemNav();
            int cor = getColor(ativo ? R.color.branco : R.color.suave);
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
    // Saldo de demonstração
    // ------------------------------------------------------------------

    protected void atualizarCarteira() {
        TextView valor = findViewById(R.id.carteira_valor);
        if (valor != null) {
            SpannableStringBuilder texto = new SpannableStringBuilder(getString(R.string.saldo_curto) + " ");
            negrito(texto, Formato.dinheiro(usuario().getCarteira().getSaldo()));
            valor.setText(texto);
        }
    }

    /** POLIMORFISMO: a variável é do tipo Pagamento, mas o objeto é Pix, Cartão ou Boleto. */
    protected void escolherFormaDeRecarga() {
        String[] formas = {getString(R.string.forma_pix), getString(R.string.forma_cartao), getString(R.string.forma_boleto)};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.recarregar_titulo, Formato.dinheiro(Consumidor.VALOR_RECARGA)))
                .setItems(formas, (dialogo, escolha) -> {
                    double valor = Consumidor.VALOR_RECARGA;
                    Pagamento forma;
                    if (escolha == 0) {
                        forma = new PagamentoPix(valor);
                    } else if (escolha == 1) {
                        forma = new PagamentoCartao(valor, "4242");
                    } else {
                        forma = new PagamentoBoleto(valor);
                    }
                    aviso(usuario().recarregar(forma));
                    atualizarCarteira();
                    atualizar();
                })
                .show();
    }

    // ------------------------------------------------------------------
    // Ações comuns
    // ------------------------------------------------------------------

    protected Consumidor usuario() {
        return DadosApp.getUsuario();
    }

    protected Plataforma plataforma() {
        return DadosApp.getPlataforma();
    }

    /** Compra o prompt (ou adiciona, se for grátis). Avisa se faltar saldo. */
    protected void comprar(Prompt prompt) {
        try {
            usuario().comprar(prompt);
            aviso(prompt.isGratis()
                    ? getString(R.string.adicionado_gratis, prompt.getTitulo())
                    : getString(R.string.compra_concluida, prompt.getTitulo()));
            atualizarCarteira();
            atualizar();
        } catch (SaldoInsuficienteException erro) {
            aviso(erro.getMessage());
        }
    }

    /** Mecânica "Copy & Paste": copia de verdade para a área de transferência. */
    protected void copiar(Prompt prompt) {
        ClipboardManager clipboard = getSystemService(ClipboardManager.class);
        clipboard.setPrimaryClip(ClipData.newPlainText("Prompt", prompt.getConteudo()));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            aviso(R.string.copiado); // o Android 13+ já mostra a confirmação sozinho
        }
    }

    protected void abrirPrompt(Prompt prompt) {
        Intent intent = new Intent(this, PromptActivity.class);
        intent.putExtra(EXTRA_PROMPT, prompt.getId());
        startActivity(intent);
    }

    protected Prompt promptRecebido() {
        return plataforma().getPrompt(getIntent().getIntExtra(EXTRA_PROMPT, -1));
    }

    /** Lista de opções com uma marcada; chama "escolha" com a posição tocada. */
    protected void escolher(String titulo, String[] opcoes, int marcada, IntConsumer escolha) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setSingleChoiceItems(opcoes, marcada, (dialogo, posicao) -> {
                    dialogo.dismiss();
                    escolha.accept(posicao);
                })
                .show();
    }

    protected String plural(int quantidade, int textoUm, int textoVarios) {
        return getString(quantidade == 1 ? textoUm : textoVarios, Formato.numero(quantidade));
    }

    protected void aviso(int texto) {
        aviso(getString(texto));
    }

    private static Toast avisoAtual;

    /** Mostra um aviso rápido. Um aviso novo substitui o anterior (não forma fila). */
    protected void aviso(String mensagem) {
        if (avisoAtual != null) {
            avisoAtual.cancel();
        }
        avisoAtual = Toast.makeText(this, mensagem, Toast.LENGTH_SHORT);
        avisoAtual.show();
    }

    // ------------------------------------------------------------------
    // Peças visuais
    // ------------------------------------------------------------------

    protected int dp(float valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    /** Foto redonda do criador ou, se não tiver foto, as iniciais num círculo azul. */
    protected View avatar(Usuario usuario, int tamanhoDp) {
        if (usuario instanceof Criador && ((Criador) usuario).getFoto() != null) {
            ImageView foto = new ImageView(this);
            foto.setImageResource(getResources().getIdentifier(
                    ((Criador) usuario).getFoto(), "drawable", getPackageName()));
            foto.setScaleType(ImageView.ScaleType.CENTER_CROP);
            foto.setBackgroundResource(R.drawable.avatar);
            foto.setClipToOutline(true);
            return foto;
        }
        return avatarIniciais(usuario.getIniciais(), tamanhoDp);
    }

    protected TextView avatarIniciais(String iniciais, int tamanhoDp) {
        TextView circulo = new TextView(this);
        circulo.setText(iniciais);
        circulo.setGravity(Gravity.CENTER);
        circulo.setBackgroundResource(R.drawable.avatar);
        circulo.setTextColor(getColor(R.color.marca));
        circulo.setTypeface(getResources().getFont(R.font.bricolage_extrabold));
        circulo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tamanhoDp * 0.36f);
        return circulo;
    }

    /** Coloca um avatar dentro de um FrameLayout do layout. */
    protected void colocarAvatar(ViewGroup lugar, View avatar, int tamanhoDp) {
        lugar.removeAllViews();
        lugar.addView(avatar, new FrameLayout.LayoutParams(dp(tamanhoDp), dp(tamanhoDp)));
    }

    /** ★★★★☆ em dourado e cinza. */
    protected CharSequence estrelas(double nota) {
        int cheias = (int) Math.round(nota);
        SpannableStringBuilder texto = new SpannableStringBuilder();
        for (int i = 1; i <= 5; i++) {
            int inicio = texto.length();
            texto.append('★');
            texto.setSpan(new ForegroundColorSpan(getColor(i <= cheias ? R.color.estrela : R.color.estrela_vazia)),
                    inicio, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return texto;
    }

    /** "★★★★★ 4,8 (312)" ou a etiqueta "Novo" quando ninguém avaliou ainda. */
    protected CharSequence textoNota(Prompt prompt) {
        SpannableStringBuilder texto = new SpannableStringBuilder();
        if (prompt.getQuantidadeAvaliacoes() == 0) {
            int inicio = texto.length();
            texto.append(" ").append(getString(R.string.novo)).append(" ");
            texto.setSpan(new BackgroundColorSpan(getColor(R.color.marca)), inicio, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            texto.setSpan(new StyleSpan(Typeface.BOLD), inicio, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return texto;
        }
        texto.append(estrelas(prompt.getNota())).append("  ");
        negrito(texto, Formato.nota(prompt.getNota()));
        int inicio = texto.length();
        texto.append(" (").append(Formato.numero(prompt.getQuantidadeAvaliacoes())).append(")");
        texto.setSpan(new ForegroundColorSpan(getColor(R.color.suave)), inicio, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return texto;
    }

    /** Destaca em amarelo as partes entre [COLCHETES], que o comprador preenche. */
    protected SpannableStringBuilder destacarVariaveis(String prompt) {
        SpannableStringBuilder texto = new SpannableStringBuilder(prompt);
        Matcher variavel = Pattern.compile("\\[[^\\]]+\\]").matcher(prompt);
        while (variavel.find()) {
            texto.setSpan(new BackgroundColorSpan(getColor(R.color.marca)),
                    variavel.start(), variavel.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            texto.setSpan(new StyleSpan(Typeface.BOLD), variavel.start(), variavel.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return texto;
    }

    protected void negrito(SpannableStringBuilder texto, String parte) {
        int inicio = texto.length();
        texto.append(parte);
        texto.setSpan(new StyleSpan(Typeface.BOLD), inicio, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /** Etiqueta colorida (categoria, "Na sua biblioteca"...). */
    protected TextView etiqueta(String texto, int fundo, int cor) {
        TextView etiqueta = new TextView(this);
        etiqueta.setText(texto);
        etiqueta.setBackgroundResource(fundo);
        etiqueta.setTextColor(getColor(cor));
        etiqueta.setTextSize(12.5f);
        etiqueta.setTypeface(etiqueta.getTypeface(), Typeface.BOLD);
        etiqueta.setPadding(dp(9), dp(2), dp(9), dp(2));
        LinearLayout.LayoutParams margem = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        margem.setMarginEnd(dp(6));
        etiqueta.setLayoutParams(margem);
        return etiqueta;
    }

    /** Etiqueta das ferramentas de IA, em fonte mono. */
    protected TextView etiquetaModelo(String modelo) {
        TextView etiqueta = etiqueta(modelo, R.drawable.modelo, R.color.suave);
        etiqueta.setTypeface(getResources().getFont(R.font.jetbrains));
        etiqueta.setTextSize(11.5f);
        etiqueta.setPadding(dp(7), dp(2), dp(7), dp(2));
        return etiqueta;
    }

    /** Liga/desliga o visual de um chip (preto quando ativo, branco quando não). */
    protected void marcarChip(TextView chip, boolean ativo) {
        chip.setBackgroundResource(ativo ? R.drawable.chip_ativo : R.drawable.chip);
        chip.setTextColor(getColor(ativo ? R.color.branco : R.color.tinta));
    }

    protected TextView novoChip(String texto, boolean ativo) {
        TextView chip = new TextView(this);
        chip.setText(texto);
        chip.setTextSize(14);
        chip.setTypeface(chip.getTypeface(), Typeface.BOLD);
        chip.setGravity(Gravity.CENTER);
        chip.setMinHeight(dp(38));
        chip.setPadding(dp(16), 0, dp(16), 0);
        LinearLayout.LayoutParams margem = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        margem.setMarginEnd(dp(8));
        chip.setLayoutParams(margem);
        marcarChip(chip, ativo);
        return chip;
    }

    protected void pintarCoracao(ImageView coracao, boolean favorito) {
        coracao.setImageTintList(ColorStateList.valueOf(getColor(favorito ? R.color.coral : R.color.coracao_vazio)));
    }

    /** Caixinha de número (ex.: "205 / Vendas"). */
    protected void adicionarEstatisticas(LinearLayout lugar, int porLinha, String[]... valores) {
        lugar.removeAllViews();
        LinearLayout linha = null;
        for (int i = 0; i < valores.length; i++) {
            if (i % porLinha == 0) {
                linha = new LinearLayout(this);
                linha.setOrientation(LinearLayout.HORIZONTAL);
                lugar.addView(linha);
            }
            LinearLayout caixa = new LinearLayout(this);
            caixa.setOrientation(LinearLayout.VERTICAL);
            caixa.setBackgroundResource(R.drawable.cartao);
            caixa.setPadding(dp(14), dp(12), dp(14), dp(12));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            p.setMargins(dp(4), dp(4), dp(4), dp(4));
            caixa.setLayoutParams(p);

            TextView numero = new TextView(this);
            numero.setText(valores[i][0]);
            numero.setTypeface(getResources().getFont(R.font.bricolage_extrabold));
            numero.setTextColor(getColor(R.color.tinta));
            numero.setTextSize(porLinha >= 3 ? 16 : 20);
            numero.setMaxLines(1);
            TextView rotulo = new TextView(this);
            rotulo.setText(valores[i][1]);
            rotulo.setTextColor(getColor(R.color.suave));
            rotulo.setTextSize(12);
            caixa.addView(numero);
            caixa.addView(rotulo);
            linha.addView(caixa);
        }
    }

    // ------------------------------------------------------------------
    // Linhas de lista
    // ------------------------------------------------------------------

    /** Linha com algo à esquerda (avatar ou ícone), título, subtítulo e valor. */
    protected View adicionarLinha(LinearLayout lista, View inicio, int tamanhoInicioDp, String titulo,
                                  String sub, CharSequence valor, View.OnClickListener clique) {
        View linha = getLayoutInflater().inflate(R.layout.item_linha, lista, false);
        FrameLayout lugar = linha.findViewById(R.id.linha_inicio);
        if (inicio != null) {
            colocarAvatar(lugar, inicio, tamanhoInicioDp);
        } else {
            lugar.setVisibility(View.GONE);
        }
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

    /** Ícone azul num quadradinho azul-claro. */
    protected ImageView icone(int desenho) {
        ImageView icone = new ImageView(this);
        icone.setImageResource(desenho);
        icone.setImageTintList(ColorStateList.valueOf(getColor(R.color.azul)));
        icone.setBackgroundResource(R.drawable.icone_fundo);
        icone.setPadding(dp(9), dp(9), dp(9), dp(9));
        return icone;
    }

    private void preencher(TextView campo, CharSequence texto) {
        if (texto == null || texto.length() == 0) {
            campo.setVisibility(View.GONE);
        } else {
            campo.setText(texto);
        }
    }

    /** Lê um número digitado ("19,90" ou "19.90"). Devolve -1 se não for número. */
    protected double lerNumero(String texto) {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException erro) {
            return -1;
        }
    }
}
