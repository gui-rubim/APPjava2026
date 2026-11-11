package com.promptplace.telas;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.Formato;
import com.promptplace.model.Simulador;

/** Modelo de negócio: fontes de receita, simulador e roadmap. Fundo azul como no protótipo. */
public class NegocioActivity extends TelaBase {

    private SeekBar barraVendas;
    private SeekBar barraTicket;

    @Override
    protected boolean topoAzul() {
        return true;
    }

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_negocio);
        setTitle(R.string.negocio_titulo);
        ((TextView) findViewById(R.id.txt_topo_titulo)).setTextColor(getColor(R.color.branco));

        // Fontes de receita
        LinearLayout receitas = findViewById(R.id.lista_receitas);
        receita(receitas, "15%", R.string.receita_1, R.string.receita_1_sub);
        receita(receitas, "10%", R.string.receita_2, R.string.receita_2_sub);
        receita(receitas, "R$ 29", R.string.receita_3, R.string.receita_3_sub);
        receita(receitas, "5%", R.string.receita_4, R.string.receita_4_sub);

        // Simulador
        barraVendas = findViewById(R.id.seek_vendas);
        barraTicket = findViewById(R.id.seek_ticket);
        SeekBar.OnSeekBarChangeListener mudou = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar s, int p, boolean u) {
                simular();
            }

            @Override
            public void onStartTrackingTouch(SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar s) {
            }
        };
        barraVendas.setOnSeekBarChangeListener(mudou);
        barraTicket.setOnSeekBarChangeListener(mudou);
        simular();

        // Roadmap
        LinearLayout fases = findViewById(R.id.lista_fases);
        fase(fases, R.string.fase_1, R.string.fase_1_itens);
        fase(fases, R.string.fase_2, R.string.fase_2_itens);
        fase(fases, R.string.fase_3, R.string.fase_3_itens);
    }

    private void simular() {
        int vendas = 100 + barraVendas.getProgress() * 100;   // de 100 a 20.000
        int ticket = 5 + barraTicket.getProgress();            // de R$ 5 a R$ 60
        Simulador simulador = new Simulador(vendas, ticket);
        ((TextView) findViewById(R.id.txt_sim_vendas)).setText(Formato.numero(vendas));
        ((TextView) findViewById(R.id.txt_sim_ticket)).setText(Formato.dinheiro(ticket));
        ((TextView) findViewById(R.id.txt_sim_volume)).setText(Formato.dinheiro(simulador.getVolume()));
        ((TextView) findViewById(R.id.txt_sim_repasse)).setText(Formato.dinheiro(simulador.getRepasseCriadores()));
        ((TextView) findViewById(R.id.txt_sim_receita)).setText(Formato.dinheiro(simulador.getReceitaPlataforma()));
    }

    private void receita(LinearLayout lista, String numero, int titulo, int descricao) {
        LinearLayout linha = new LinearLayout(this);
        linha.setPadding(0, dp(16), 0, dp(16));

        TextView grande = new TextView(this);
        grande.setText(numero);
        grande.setTypeface(getResources().getFont(R.font.bricolage_extrabold));
        grande.setTextColor(getColor(R.color.marca));
        grande.setTextSize(26);
        linha.addView(grande, new LinearLayout.LayoutParams(dp(96), LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.addView(texto(getString(titulo), R.color.branco, 16, true));
        textos.addView(texto(getString(descricao), R.color.branco_80, 13.5f, false));
        linha.addView(textos, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        lista.addView(linha);
    }

    private void fase(LinearLayout lista, int titulo, int itens) {
        LinearLayout linha = new LinearLayout(this);
        linha.setPadding(0, dp(14), 0, dp(4));

        View bolinha = new View(this);
        bolinha.setBackgroundResource(R.drawable.circulo_amarelo);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(12), dp(12));
        p.topMargin = dp(5);
        p.setMarginEnd(dp(14));
        linha.addView(bolinha, p);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.addView(texto(getString(titulo), R.color.branco, 17, true));
        TextView lista2 = texto(getString(itens), R.color.branco_80, 14, false);
        lista2.setLineSpacing(dp(4), 1);
        lista2.setPadding(0, dp(4), 0, 0);
        textos.addView(lista2);
        linha.addView(textos);
        lista.addView(linha);
    }

    private TextView texto(String conteudo, int cor, float tamanho, boolean negrito) {
        TextView texto = new TextView(this);
        texto.setText(conteudo);
        texto.setTextColor(getColor(cor));
        texto.setTextSize(tamanho);
        texto.setGravity(Gravity.START);
        if (negrito) {
            texto.setTypeface(getResources().getFont(R.font.bricolage_extrabold));
        }
        return texto;
    }
}
