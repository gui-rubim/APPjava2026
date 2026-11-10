package com.promptplace.telas;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.DadosApp;
import com.promptplace.R;
import com.promptplace.model.Criador;
import com.promptplace.model.Formato;
import com.promptplace.model.Prompt;

/** Perfil do criador: assinatura, teste cego e cofre de prompts. */
public class CriadorActivity extends TelaBase {

    private Criador criador;
    private LinearLayout lista;
    private Button btnTesteCego;
    private TextView btnAssinar;

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_criador);
        criador = criadorRecebido();
        setTitle(R.string.criador);

        lista = findViewById(R.id.lista);
        btnTesteCego = findViewById(R.id.btn_teste_cego);
        btnAssinar = findViewById(R.id.btn_assinar);

        ImageView foto = findViewById(R.id.img_foto);
        foto.setImageResource(fotoDe(criador));
        recortar(foto);

        ((TextView) findViewById(R.id.txt_nome)).setText(criador.getNome());
        // POLIMORFISMO: exibirPerfil() aqui é o do Criador.
        ((TextView) findViewById(R.id.txt_resumo)).setText(criador.exibirPerfil());
        ((TextView) findViewById(R.id.txt_bio)).setText(criador.getBio());

        btnTesteCego.setOnClickListener(v -> fazerTesteCego());
        btnAssinar.setOnClickListener(v -> {
            if (usuario().assina(criador)) {
                abrirBiblioteca();
                return;
            }
            Intent intent = new Intent(this, PagamentoActivity.class);
            intent.putExtra(EXTRA_CRIADOR, DadosApp.getPlataforma().getCriadores().indexOf(criador));
            startActivity(intent);
        });
    }

    private void fazerTesteCego() {
        Prompt sorteado = usuario().fazerTesteCego(criador);
        if (sorteado == null) {
            aviso(R.string.teste_cego_ja_usou);
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.surpresa)
                .setMessage(getString(R.string.surpresa_texto, sorteado.getTitulo()))
                .setPositiveButton(R.string.ver_prompt, (d, w) -> abrirPrompt(sorteado))
                .setNegativeButton(R.string.fechar, null)
                .show();
        atualizar();
    }

    @Override
    protected void atualizar() {
        boolean assinante = usuario().assina(criador);
        String primeiroNome = criador.getNome().split(" ")[0];

        // Números
        ((TextView) findViewById(R.id.txt_qtd)).setText(String.valueOf(criador.getCofre().size()));
        TextView media = findViewById(R.id.txt_media);
        if (criador.getTotalAvaliacoes() == 0) {
            media.setText(R.string.novo);
            media.setCompoundDrawablesRelative(null, null, null, null);
        } else {
            media.setText(Formato.texto("%.1f", criador.getNotaMedia()));
        }
        ((TextView) findViewById(R.id.txt_avaliacoes)).setText(String.valueOf(criador.getTotalAvaliacoes()));

        // Cartão de assinatura
        TextView titulo = findViewById(R.id.txt_assinatura_titulo);
        TextView texto = findViewById(R.id.txt_assinatura_texto);
        if (assinante) {
            titulo.setText(R.string.voce_assinante);
            titulo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            texto.setText(getString(R.string.assinante_texto, primeiroNome));
            btnAssinar.setText(R.string.abrir_biblioteca);
        } else {
            titulo.setText(getString(R.string.cofre_de, primeiroNome));
            texto.setText(getString(R.string.cofre_texto, primeiroNome));
            btnAssinar.setText(getString(R.string.assinar_por, criador.getPrecoFormatado()));
        }

        // Teste cego
        if (assinante) {
            btnTesteCego.setVisibility(View.GONE);
        } else if (usuario().jaUsouTesteCego(criador)) {
            btnTesteCego.setText(R.string.teste_cego_usado);
            ativarBotao(btnTesteCego, false);
        }

        // Cofre
        lista.removeAllViews();
        for (Prompt p : criador.getCofre()) {
            boolean liberado = usuario().getBiblioteca().contem(p);
            View linha;
            if (liberado) {
                linha = adicionarLinhaIcone(lista, R.drawable.ic_cadeado_aberto, R.color.azul_claro,
                        p.getTitulo(), getString(R.string.liberado), p.getTextoNota(),
                        v -> abrirPrompt(p));
            } else {
                linha = adicionarLinhaIcone(lista, R.drawable.ic_cadeado, R.color.texto_3,
                        p.getTitulo(), getString(R.string.bloqueado), p.getTextoNota(),
                        v -> aviso(R.string.aviso_bloqueado));
            }
            if (!p.getAvaliacoes().isEmpty()) {
                comEstrela(linha);
            }
        }
    }
}
