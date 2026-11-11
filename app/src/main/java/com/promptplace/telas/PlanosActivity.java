package com.promptplace.telas;

import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.AssinaturaCriador;
import com.promptplace.model.Cobranca;
import com.promptplace.model.Criador;
import com.promptplace.model.Formato;
import com.promptplace.model.Plano;
import com.promptplace.model.SaldoInsuficienteException;

import java.util.ArrayList;
import java.util.List;

/** Assinaturas (planos Gratuito, Pro e Equipes) e doações para criadores. */
public class PlanosActivity extends TelaBase {

    private Cobranca cobranca = Cobranca.MENSAL;
    private double valorDoacao = 10;
    private Criador criador;
    private TextView[] chipsValor;
    private EditText campoOutroValor;

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_planos);
        setTitle(R.string.planos_titulo);

        findViewById(R.id.aba_mensal).setOnClickListener(v -> {
            cobranca = Cobranca.MENSAL;
            atualizar();
        });
        findViewById(R.id.aba_anual).setOnClickListener(v -> {
            cobranca = Cobranca.ANUAL;
            atualizar();
        });

        List<Criador> criadores = criadoresParaApoiar();
        criador = criadores.get(0);
        TextView botaoCriador = findViewById(R.id.btn_escolher_criador);
        botaoCriador.setOnClickListener(v -> {
            String[] nomes = new String[criadores.size()];
            for (int i = 0; i < nomes.length; i++) {
                nomes[i] = criadores.get(i).getNome();
            }
            escolher(getString(R.string.campo_criador), nomes, criadores.indexOf(criador), posicao -> {
                criador = criadores.get(posicao);
                atualizar();
            });
        });

        chipsValor = new TextView[] {findViewById(R.id.valor_5), findViewById(R.id.valor_10), findViewById(R.id.valor_25)};
        double[] valores = {5, 10, 25};
        campoOutroValor = findViewById(R.id.edt_outro_valor);
        for (int i = 0; i < chipsValor.length; i++) {
            double valor = valores[i];
            chipsValor[i].setOnClickListener(v -> {
                valorDoacao = valor;
                campoOutroValor.setText("");
                atualizar();
            });
        }
        campoOutroValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
            }

            @Override
            public void afterTextChanged(Editable texto) {
                if (texto.length() > 0) {
                    valorDoacao = Math.max(lerNumero(texto.toString()), 0);
                    marcarValores();
                }
            }
        });

        findViewById(R.id.btn_doar).setOnClickListener(v -> {
            try {
                usuario().doar(criador, valorDoacao);
                aviso(getString(R.string.obrigado_doacao, Formato.dinheiro(valorDoacao), criador.getNome()));
            } catch (IllegalArgumentException | SaldoInsuficienteException erro) {
                aviso(erro.getMessage());
            }
        });
        findViewById(R.id.btn_assinar_criador).setOnClickListener(v -> {
            try {
                usuario().assinarCriador(criador);
                aviso(getString(R.string.assinou_criador, criador.getNome(), Formato.dinheiro(AssinaturaCriador.PRECO)));
            } catch (SaldoInsuficienteException erro) {
                aviso(erro.getMessage());
            }
        });
    }

    /** Os criadores do marketplace (sem o perfil de quem está usando o app). */
    private List<Criador> criadoresParaApoiar() {
        List<Criador> lista = new ArrayList<>(plataforma().getCriadores());
        lista.remove(usuario().getPerfilCriador());
        return lista;
    }

    private void marcarValores() {
        double[] valores = {5, 10, 25};
        boolean digitado = campoOutroValor.getText().length() > 0;
        for (int i = 0; i < chipsValor.length; i++) {
            marcarChip(chipsValor[i], !digitado && valores[i] == valorDoacao);
        }
    }

    @Override
    protected void atualizar() {
        marcarChip(findViewById(R.id.aba_mensal), cobranca == Cobranca.MENSAL);
        marcarChip(findViewById(R.id.aba_anual), cobranca == Cobranca.ANUAL);

        LinearLayout lista = findViewById(R.id.lista_planos);
        lista.removeAllViews();
        for (Plano plano : Plano.values()) {
            lista.addView(cartao(plano));
        }

        ((TextView) findViewById(R.id.btn_escolher_criador)).setText(criador.getNome());
        ((Button) findViewById(R.id.btn_assinar_criador)).setText(
                getString(R.string.assinar_criador, Formato.dinheiro(AssinaturaCriador.PRECO)));
        marcarValores();
    }

    private View cartao(Plano plano) {
        View cartao = getLayoutInflater().inflate(R.layout.item_plano, null, false);
        boolean destaque = plano == Plano.PRO;
        boolean gratis = plano.getPrecoMensal() == 0;
        boolean atual = usuario().isPlanoAtual(plano, cobranca);

        cartao.findViewById(R.id.plano_cartao).setBackgroundResource(destaque ? R.drawable.cartao_destaque : R.drawable.cartao);
        cartao.findViewById(R.id.plano_tag).setVisibility(destaque ? View.VISIBLE : View.GONE);
        ((TextView) cartao.findViewById(R.id.plano_nome)).setText(plano.getNome());

        String preco = Formato.dinheiro(plano.getPrecoPorMes(cobranca)).replace(",00", "").replace(".00", "");
        ((TextView) cartao.findViewById(R.id.plano_preco)).setText(preco);
        cartao.findViewById(R.id.plano_por_mes).setVisibility(gratis ? View.GONE : View.VISIBLE);
        ((TextView) cartao.findViewById(R.id.plano_nota)).setText(gratis
                ? getString(R.string.plano_nota_gratis)
                : cobranca == Cobranca.ANUAL
                        ? getString(R.string.plano_nota_anual, Formato.dinheiro(plano.getPrecoTotal(cobranca)))
                        : getString(R.string.plano_nota_mensal));

        LinearLayout recursos = cartao.findViewById(R.id.plano_recursos);
        for (String recurso : plano.getRecursos()) {
            LinearLayout linha = new LinearLayout(this);
            linha.setGravity(Gravity.CENTER_VERTICAL);
            linha.setPadding(0, dp(3), 0, dp(3));
            ImageView check = new ImageView(this);
            check.setImageResource(R.drawable.ic_check_simples);
            check.setImageTintList(ColorStateList.valueOf(getColor(R.color.menta)));
            linha.addView(check, new LinearLayout.LayoutParams(dp(18), dp(18)));
            TextView texto = new TextView(this);
            texto.setText(recurso);
            texto.setTextColor(getColor(R.color.tinta));
            texto.setTextSize(14);
            texto.setPadding(dp(10), 0, 0, 0);
            linha.addView(texto);
            recursos.addView(linha);
        }

        Button botao = cartao.findViewById(R.id.plano_botao);
        if (destaque) {
            botao.setBackgroundResource(R.drawable.botao_primario);
            botao.setTextColor(getColor(R.color.branco));
        }
        if (atual) {
            botao.setText(R.string.plano_atual);
            ativar(botao, false);
        } else {
            botao.setText(gratis ? getString(R.string.usar_gratuito) : getString(R.string.assinar_plano, plano.getNome()));
            botao.setOnClickListener(v -> {
                try {
                    usuario().assinarPlano(plano, cobranca);
                    aviso(getString(R.string.plano_ativado, plano.getNome()));
                    atualizar();
                } catch (SaldoInsuficienteException erro) {
                    aviso(erro.getMessage());
                }
            });
        }
        LinearLayout.LayoutParams margem = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        margem.bottomMargin = dp(6);
        cartao.setLayoutParams(margem);
        return cartao;
    }

    private void ativar(View botao, boolean ativo) {
        botao.setEnabled(ativo);
        botao.setAlpha(ativo ? 1f : 0.5f);
    }
}
