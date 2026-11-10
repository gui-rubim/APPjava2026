package com.promptplace.telas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.promptplace.ConfigIdioma;
import com.promptplace.DadosApp;
import com.promptplace.R;
import com.promptplace.model.Idioma;

/** Tela de entrada: pede nome e e-mail (login de demonstração, sem senha). */
public class LoginActivity extends Activity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ConfigIdioma.aplicar(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Seletor de idioma EN | PT
        TextView btnIngles = findViewById(R.id.btn_en);
        TextView btnPortugues = findViewById(R.id.btn_pt);
        marcar(btnIngles, Idioma.getAtual() == Idioma.INGLES);
        marcar(btnPortugues, Idioma.getAtual() == Idioma.PORTUGUES);
        btnIngles.setOnClickListener(v -> trocarIdioma(Idioma.INGLES));
        btnPortugues.setOnClickListener(v -> trocarIdioma(Idioma.PORTUGUES));

        EditText campoNome = findViewById(R.id.edt_nome);
        EditText campoEmail = findViewById(R.id.edt_email);

        findViewById(R.id.btn_entrar).setOnClickListener(v -> {
            String nome = campoNome.getText().toString().trim();
            String email = campoEmail.getText().toString().trim();

            if (nome.isEmpty() || !email.contains("@")) {
                Toast.makeText(this, R.string.erro_login, Toast.LENGTH_SHORT).show();
                return;
            }

            DadosApp.entrar(nome, email);
            startActivity(new Intent(this, FeedActivity.class));
            finish();
        });
    }

    private void marcar(TextView botao, boolean ativo) {
        botao.setBackgroundResource(ativo ? R.drawable.segmento_ativo : 0);
        botao.setTextColor(getColor(ativo ? R.color.branco : R.color.texto_2));
    }

    private void trocarIdioma(Idioma idioma) {
        if (idioma != Idioma.getAtual()) {
            ConfigIdioma.salvar(this, idioma);
            recreate(); // abre a tela de novo, já no idioma novo
        }
    }
}
