package com.promptplace.telas;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.ConfigIdioma;
import com.promptplace.DadosApp;
import com.promptplace.R;
import com.promptplace.model.Assinatura;
import com.promptplace.model.Criador;
import com.promptplace.model.Formato;
import com.promptplace.model.Idioma;
import com.promptplace.model.Plataforma;

/** Perfil do usuário, assinaturas, painel financeiro (70/30) e idioma. */
public class PerfilActivity extends TelaBase {

    @Override
    protected int itemNav() {
        return R.id.nav_perfil;
    }

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_perfil);

        ((TextView) findViewById(R.id.txt_iniciais)).setText(usuario().getIniciais());
        ((TextView) findViewById(R.id.txt_nome)).setText(usuario().getNome());
        ((TextView) findViewById(R.id.txt_email)).setText(usuario().getEmail());

        // Idioma: inglês é o principal, português é o secundário.
        LinearLayout idiomas = findViewById(R.id.lista_idiomas);
        adicionarIdioma(idiomas, Idioma.INGLES, R.string.idioma_ingles, R.string.idioma_ingles_sub);
        adicionarIdioma(idiomas, Idioma.PORTUGUES, R.string.idioma_portugues, R.string.idioma_portugues_sub);

        findViewById(R.id.btn_sair).setOnClickListener(v -> {
            DadosApp.sair();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void adicionarIdioma(LinearLayout lista, Idioma idioma, int nome, int descricao) {
        boolean ativo = idioma == Idioma.getAtual();
        View linha = adicionarLinhaIcone(lista, R.drawable.ic_idioma,
                ativo ? R.color.azul_claro : R.color.texto_3,
                getString(nome), getString(descricao),
                ativo ? getString(R.string.ativo) : null,
                ativo ? null : v -> trocarIdioma(idioma));
        if (ativo) {
            ((TextView) linha.findViewById(R.id.linha_valor)).setTextColor(getColor(R.color.azul_claro));
        }
    }

    /** Salva o idioma e reabre o app (Início + Perfil) já traduzido. */
    private void trocarIdioma(Idioma idioma) {
        ConfigIdioma.salvar(this, idioma);
        Intent inicio = new Intent(this, FeedActivity.class);
        inicio.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Intent perfil = new Intent(this, PerfilActivity.class);
        startActivities(new Intent[] {inicio, perfil});
        overridePendingTransition(0, 0);
    }

    @Override
    protected void atualizar() {
        // POLIMORFISMO: exibirPerfil() aqui é o do Consumidor.
        ((TextView) findViewById(R.id.txt_resumo)).setText(usuario().exibirPerfil());

        LinearLayout assinaturas = findViewById(R.id.lista_assinaturas);
        assinaturas.removeAllViews();
        if (usuario().getAssinaturas().isEmpty()) {
            adicionarLinhaIcone(assinaturas, R.drawable.ic_biblioteca, R.color.texto_3,
                    getString(R.string.nenhuma_assinatura), getString(R.string.escolha_criador), null, null);
        }
        for (Assinatura a : usuario().getAssinaturas()) {
            adicionarLinhaFoto(assinaturas, a.getCriador(), a.getCriador().getNome(),
                    getString(R.string.pago_via, a.getPagamento().getNome()),
                    a.getCriador().getPrecoFormatado(),
                    v -> abrirCriador(a.getCriador()));
        }

        ((TextView) findViewById(R.id.txt_receita)).setText(
                Formato.texto("R$ %.2f", Plataforma.getReceita()));

        LinearLayout financeiro = findViewById(R.id.lista_financeiro);
        financeiro.removeAllViews();
        for (Criador c : DadosApp.getPlataforma().getCriadores()) {
            adicionarLinhaFoto(financeiro, c, c.getNome(), getString(R.string.saldo_criador),
                    Formato.texto("R$ %.2f", c.getSaldo()), null);
        }
    }
}
