package com.promptplace.telas;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.promptplace.ConfigIdioma;
import com.promptplace.DadosApp;
import com.promptplace.R;
import com.promptplace.model.Formato;
import com.promptplace.model.Idioma;

/** Mais: conta, saldo de demonstração, os outros módulos, idioma e reiniciar. */
public class MaisActivity extends TelaBase {

    @Override
    protected int itemNav() {
        return R.id.nav_mais;
    }

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_mais);
        findViewById(R.id.btn_recarregar).setOnClickListener(v -> escolherFormaDeRecarga());

        LinearLayout modulos = findViewById(R.id.lista_modulos);
        modulo(modulos, R.drawable.ic_planos, R.string.planos_titulo, R.string.planos_resumo, PlanosActivity.class);
        modulo(modulos, R.drawable.ic_encomenda, R.string.comissoes_titulo, R.string.comissoes_resumo, ComissoesActivity.class);
        modulo(modulos, R.drawable.ic_historico, R.string.historico_titulo, R.string.historico_resumo, HistoricoActivity.class);
        modulo(modulos, R.drawable.ic_grafico, R.string.negocio_titulo, R.string.negocio_resumo, NegocioActivity.class);

        LinearLayout idiomas = findViewById(R.id.lista_idiomas);
        idioma(idiomas, Idioma.INGLES, R.string.idioma_ingles, R.string.idioma_ingles_sub);
        idioma(idiomas, Idioma.PORTUGUES, R.string.idioma_portugues, R.string.idioma_portugues_sub);

        LinearLayout demo = findViewById(R.id.lista_demo);
        adicionarLinha(demo, icone(R.drawable.ic_reiniciar), 40, getString(R.string.reiniciar),
                getString(R.string.reiniciar_sub), null, v -> confirmarReinicio());
        adicionarLinha(demo, icone(R.drawable.ic_sair), 40, getString(R.string.sair), null, null, v -> {
            DadosApp.sair();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void modulo(LinearLayout lista, int desenho, int titulo, int resumo, Class<?> tela) {
        adicionarLinha(lista, icone(desenho), 40, getString(titulo), getString(resumo), null,
                v -> startActivity(new Intent(this, tela)));
    }

    private void idioma(LinearLayout lista, Idioma idioma, int nome, int descricao) {
        boolean ativo = idioma == Idioma.getAtual();
        View linha = adicionarLinha(lista, icone(R.drawable.ic_idioma), 40, getString(nome), getString(descricao),
                ativo ? getString(R.string.ativo) : null, ativo ? null : v -> trocarIdioma(idioma));
        if (ativo) {
            TextView valor = linha.findViewById(R.id.linha_valor);
            valor.setTextColor(getColor(R.color.azul));
            valor.setTypeface(valor.getTypeface(), android.graphics.Typeface.BOLD);
        }
    }

    /** Salva o idioma e reabre o app (Marketplace + Mais) já traduzido. */
    private void trocarIdioma(Idioma idioma) {
        ConfigIdioma.salvar(this, idioma);
        reabrir();
    }

    private void confirmarReinicio() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.reiniciar)
                .setMessage(R.string.reiniciar_pergunta)
                .setPositiveButton(R.string.reiniciar, (d, w) -> {
                    DadosApp.reiniciar();
                    aviso(R.string.reiniciado);
                    reabrir();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void reabrir() {
        Intent inicio = new Intent(this, MarketplaceActivity.class);
        inicio.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivities(new Intent[] {inicio, new Intent(this, MaisActivity.class)});
        overridePendingTransition(0, 0);
    }

    @Override
    protected void atualizar() {
        colocarAvatar(findViewById(R.id.avatar_usuario), avatar(usuario(), 56), 56);
        ((TextView) findViewById(R.id.txt_nome)).setText(usuario().getNome());
        ((TextView) findViewById(R.id.txt_email)).setText(usuario().getEmail());
        // POLIMORFISMO: exibirPerfil() aqui é o do Consumidor.
        ((TextView) findViewById(R.id.txt_resumo)).setText(usuario().exibirPerfil());
        ((TextView) findViewById(R.id.txt_saldo)).setText(Formato.dinheiro(usuario().getCarteira().getSaldo()));
    }
}
