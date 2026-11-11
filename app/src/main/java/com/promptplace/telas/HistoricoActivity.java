package com.promptplace.telas;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.Formato;
import com.promptplace.model.Historico;
import com.promptplace.model.TipoTransacao;
import com.promptplace.model.Transacao;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** Histórico de compras: extrato com todas as transações e exportação em CSV. */
public class HistoricoActivity extends TelaBase {

    private static final int SALVAR_CSV = 1;

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_historico);
        setTitle(R.string.historico_titulo);

        // Abre o "Salvar como" do Android para escolher onde guardar o arquivo.
        findViewById(R.id.btn_exportar).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.arquivo_csv));
            startActivityForResult(intent, SALVAR_CSV);
        });
    }

    @Override
    protected void onActivityResult(int pedido, int resultado, Intent dados) {
        super.onActivityResult(pedido, resultado, dados);
        if (pedido != SALVAR_CSV || resultado != RESULT_OK || dados == null || dados.getData() == null) {
            return;
        }
        Uri arquivo = dados.getData();
        try (OutputStream saida = getContentResolver().openOutputStream(arquivo)) {
            saida.write(usuario().getHistorico().gerarCsv().getBytes(StandardCharsets.UTF_8));
            aviso(R.string.extrato_exportado);
        } catch (IOException | NullPointerException erro) {
            aviso(R.string.erro_exportar);
        }
    }

    @Override
    protected void atualizar() {
        Historico historico = usuario().getHistorico();
        adicionarEstatisticas(findViewById(R.id.estatisticas), 3,
                new String[] {Formato.dinheiro(historico.getTotal()), getString(R.string.total_movimentado)},
                new String[] {String.valueOf(historico.getTransacoes().size()), getString(R.string.transacoes)},
                new String[] {usuario().getPlano().getNome(), getString(R.string.plano_atual_rotulo)});

        LinearLayout lista = findViewById(R.id.lista);
        lista.removeAllViews();
        for (Transacao t : historico.getTransacoes()) {
            View linha = getLayoutInflater().inflate(R.layout.item_transacao, lista, false);
            ((TextView) linha.findViewById(R.id.tr_item)).setText(t.getItem());
            ((TextView) linha.findViewById(R.id.tr_data)).setText(Formato.data(t.getData()));
            ((TextView) linha.findViewById(R.id.tr_valor)).setText(Formato.dinheiro(t.getValor()));
            ((TextView) linha.findViewById(R.id.tr_situacao)).setText(t.getSituacao().getNome());

            TextView tipo = linha.findViewById(R.id.tr_tipo);
            tipo.setText(t.getTipo().getNome());
            pintarTipo(tipo, t.getTipo());
            lista.addView(linha);
        }
    }

    /** Cada tipo de transação tem uma cor, como no protótipo. */
    private void pintarTipo(TextView etiqueta, TipoTransacao tipo) {
        switch (tipo) {
            case COMPRA:
                etiqueta.setBackgroundResource(R.drawable.tag);
                etiqueta.setTextColor(getColor(R.color.azul));
                break;
            case ASSINATURA:
                etiqueta.setBackgroundResource(R.drawable.tag_roxa);
                etiqueta.setTextColor(getColor(R.color.roxo));
                break;
            case DOACAO:
                etiqueta.setBackgroundResource(R.drawable.tag_coral);
                etiqueta.setTextColor(getColor(R.color.coral));
                break;
            default:
                etiqueta.setBackgroundResource(R.drawable.tag_verde);
                etiqueta.setTextColor(getColor(R.color.menta));
                break;
        }
    }
}
