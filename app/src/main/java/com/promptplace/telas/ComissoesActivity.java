package com.promptplace.telas;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.Criador;
import com.promptplace.model.Encomenda;
import com.promptplace.model.EtapaEncomenda;
import com.promptplace.model.Formato;
import com.promptplace.model.SaldoInsuficienteException;

import java.util.ArrayList;
import java.util.List;

/** Comissões personalizadas: encomendar um prompt sob medida com pagamento em custódia. */
public class ComissoesActivity extends TelaBase {

    private static final int ORCAMENTO_MINIMO = 30;
    private static final int PASSO = 10;
    private static final int[] PRAZOS = {3, 7, 15};

    private Criador criador;
    private int orcamento = 90;
    private int prazo = 7;
    private TextView[] chipsPrazo;

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_comissoes);
        setTitle(R.string.comissoes_titulo);

        // Como funciona (4 passos)
        LinearLayout passos = findViewById(R.id.lista_passos);
        int[][] textos = {
                {R.string.passo_1, R.string.passo_1_sub}, {R.string.passo_2, R.string.passo_2_sub},
                {R.string.passo_3, R.string.passo_3_sub}, {R.string.passo_4, R.string.passo_4_sub}};
        for (int i = 0; i < textos.length; i++) {
            TextView numero = new TextView(this);
            numero.setText(String.valueOf(i + 1));
            numero.setGravity(Gravity.CENTER);
            numero.setBackgroundResource(R.drawable.circulo_tinta);
            numero.setTextColor(getColor(R.color.branco));
            numero.setTextSize(13);
            numero.setTypeface(numero.getTypeface(), android.graphics.Typeface.BOLD);
            adicionarLinha(passos, numero, 28, getString(textos[i][0]), getString(textos[i][1]), null, null);
        }

        List<Criador> criadores = new ArrayList<>(plataforma().getCriadores());
        criadores.remove(usuario().getPerfilCriador());
        criador = criadores.get(0);
        TextView botaoCriador = findViewById(R.id.btn_escolher_criador);
        botaoCriador.setOnClickListener(v -> {
            String[] nomes = new String[criadores.size()];
            for (int i = 0; i < nomes.length; i++) {
                nomes[i] = criadores.get(i).getNome() + " (" + criadores.get(i).getHandle() + ")";
            }
            escolher(getString(R.string.campo_criador), nomes, criadores.indexOf(criador), posicao -> {
                criador = criadores.get(posicao);
                atualizarFormulario();
            });
        });

        SeekBar barra = findViewById(R.id.seek_orcamento);
        barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar s, int progresso, boolean doUsuario) {
                orcamento = ORCAMENTO_MINIMO + progresso * PASSO;
                atualizarFormulario();
            }

            @Override
            public void onStartTrackingTouch(SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar s) {
            }
        });

        chipsPrazo = new TextView[] {findViewById(R.id.prazo_3), findViewById(R.id.prazo_7), findViewById(R.id.prazo_15)};
        for (int i = 0; i < chipsPrazo.length; i++) {
            int dias = PRAZOS[i];
            chipsPrazo[i].setText(getString(R.string.prazo_dias, dias));
            chipsPrazo[i].setOnClickListener(v -> {
                prazo = dias;
                atualizarFormulario();
            });
        }

        findViewById(R.id.btn_enviar).setOnClickListener(v -> enviar());
        atualizarFormulario();
    }

    private void atualizarFormulario() {
        ((TextView) findViewById(R.id.btn_escolher_criador)).setText(criador.getNome() + " (" + criador.getHandle() + ")");
        ((TextView) findViewById(R.id.txt_orcamento)).setText(getString(R.string.orcamento_valor, Formato.dinheiro(orcamento)));
        for (int i = 0; i < chipsPrazo.length; i++) {
            marcarChip(chipsPrazo[i], PRAZOS[i] == prazo);
        }
    }

    private void enviar() {
        EditText briefing = findViewById(R.id.edt_briefing);
        try {
            usuario().encomendar(criador, briefing.getText().toString(), orcamento, prazo);
            briefing.setText("");
            aviso(R.string.encomenda_enviada);
            atualizar();
        } catch (IllegalArgumentException | SaldoInsuficienteException erro) {
            aviso(erro.getMessage());
        }
    }

    @Override
    protected void atualizar() {
        LinearLayout lista = findViewById(R.id.lista_encomendas);
        lista.removeAllViews();
        List<Encomenda> encomendas = usuario().getEncomendas();
        if (encomendas.isEmpty()) {
            TextView vazio = new TextView(this);
            vazio.setText(R.string.sem_encomendas);
            vazio.setBackgroundResource(R.drawable.cartao);
            vazio.setGravity(Gravity.CENTER);
            vazio.setPadding(dp(20), dp(24), dp(20), dp(24));
            vazio.setTextColor(getColor(R.color.suave));
            lista.addView(vazio);
            return;
        }
        for (Encomenda e : encomendas) {
            lista.addView(cartao(e));
        }
    }

    private View cartao(Encomenda e) {
        View cartao = getLayoutInflater().inflate(R.layout.item_encomenda, lista(), false);
        ((TextView) cartao.findViewById(R.id.enc_criador)).setText(e.getCriador().getNome());
        ((TextView) cartao.findViewById(R.id.enc_info)).setText(getString(R.string.encomenda_info,
                Formato.dinheiro(e.getOrcamento()), getString(R.string.prazo_dias, e.getPrazoDias())));
        String briefing = e.getBriefing();
        ((TextView) cartao.findViewById(R.id.enc_briefing)).setText(
                briefing.length() > 160 ? briefing.substring(0, 160) + "…" : briefing);

        // As 5 etapas: as que já passaram ficam azuis.
        LinearLayout etapas = cartao.findViewById(R.id.enc_etapas);
        for (EtapaEncomenda etapa : EtapaEncomenda.values()) {
            View barra = new View(this);
            barra.setBackgroundResource(etapa.ordinal() <= e.getEtapa().ordinal() ? R.drawable.etapa_ativa : R.drawable.etapa);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            p.setMarginEnd(dp(5));
            etapas.addView(barra, p);
        }

        ((TextView) cartao.findViewById(R.id.enc_status)).setText(e.getEtapa().getNome());
        Button botao = cartao.findViewById(R.id.enc_botao);
        if (e.isConcluida()) {
            botao.setVisibility(View.GONE);
            cartao.findViewById(R.id.enc_liberado).setVisibility(View.VISIBLE);
        } else {
            botao.setText(e.getEtapa() == EtapaEncomenda.ENTREGUE ? R.string.aprovar_liberar : R.string.avancar_etapa);
            botao.setOnClickListener(v -> {
                e.avancar();
                if (e.isConcluida()) {
                    aviso(R.string.entrega_aprovada);
                } else if (e.getEtapa() == EtapaEncomenda.ENTREGUE) {
                    aviso(R.string.criador_entregou);
                } else {
                    aviso(getString(R.string.etapa_atual, e.getEtapa().getNome()));
                }
                atualizar();
            });
        }
        return cartao;
    }

    private LinearLayout lista() {
        return findViewById(R.id.lista_encomendas);
    }
}
