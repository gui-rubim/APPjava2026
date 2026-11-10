package com.promptplace.telas;

import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.Criador;
import com.promptplace.model.Formato;
import com.promptplace.model.Pagamento;
import com.promptplace.model.PagamentoBoleto;
import com.promptplace.model.PagamentoCartao;
import com.promptplace.model.PagamentoPix;

/** Tela de pagamento: escolhe Pix, Cartão ou Boleto e assina o criador. */
public class PagamentoActivity extends TelaBase {

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_pagamento);
        setTitle(R.string.pagamento);

        Criador criador = criadorRecebido();
        double valor = criador.getPrecoAssinatura();

        ImageView foto = findViewById(R.id.img_foto);
        foto.setImageResource(fotoDe(criador));
        recortar(foto);
        ((TextView) findViewById(R.id.txt_nome)).setText(criador.getNome());
        ((TextView) findViewById(R.id.txt_valor)).setText(criador.getPrecoFormatado());

        // Divisão 70% / 30% (mesma conta da classe Pagamento)
        LinearLayout divisao = findViewById(R.id.lista_divisao);
        adicionarLinhaFoto(divisao, criador, criador.getNome(), getString(R.string.criador_recebe),
                Formato.texto("R$ %.2f", valor * (1 - Pagamento.TAXA_PLATAFORMA)), null);
        adicionarLinhaIcone(divisao, R.drawable.ic_carteira, R.color.azul_claro, "PromptPlace",
                getString(R.string.plataforma_fica),
                Formato.texto("R$ %.2f", valor * Pagamento.TAXA_PLATAFORMA), null);

        RadioGroup formas = findViewById(R.id.grupo_formas);
        EditText campoCartao = findViewById(R.id.edt_cartao);
        formas.setOnCheckedChangeListener((grupo, id) ->
                campoCartao.setVisibility(id == R.id.rb_cartao ? View.VISIBLE : View.GONE));

        findViewById(R.id.btn_pagar).setOnClickListener(v -> {
            // POLIMORFISMO: a variável é do tipo Pagamento (classe abstrata),
            // mas o objeto criado pode ser Pix, Cartão ou Boleto.
            Pagamento pagamento;
            int escolhido = formas.getCheckedRadioButtonId();
            if (escolhido == R.id.rb_pix) {
                pagamento = new PagamentoPix(valor);
            } else if (escolhido == R.id.rb_cartao) {
                String finalCartao = campoCartao.getText().toString().trim();
                if (finalCartao.length() != 4) {
                    aviso(R.string.erro_cartao);
                    return;
                }
                pagamento = new PagamentoCartao(valor, finalCartao);
            } else if (escolhido == R.id.rb_boleto) {
                pagamento = new PagamentoBoleto(valor);
            } else {
                aviso(R.string.erro_forma);
                return;
            }

            String recibo = usuario().assinar(criador, pagamento);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.assinatura_ativa)
                    .setMessage(recibo + "\n\n" + getString(R.string.cofre_liberado, criador.getNome()))
                    .setCancelable(false)
                    .setPositiveButton(R.string.ver_cofre, (d, w) -> finish())
                    .show();
        });
    }
}
