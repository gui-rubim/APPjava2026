package com.promptplace.telas;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.promptplace.R;
import com.promptplace.model.Avaliacao;
import com.promptplace.model.Formato;
import com.promptplace.model.Prompt;

/** Mostra o prompt completo, com o botão "Copiar" e as avaliações. */
public class PromptActivity extends TelaBase {

    private Prompt prompt;
    private LinearLayout listaAvaliacoes;
    private TextView txtNota;

    @Override
    protected void montarTela() {
        setContentView(R.layout.activity_prompt);
        prompt = promptRecebido();

        // Só abre se o prompt estiver liberado na biblioteca.
        if (!usuario().getBiblioteca().contem(prompt)) {
            aviso(R.string.prompt_bloqueado);
            finish();
            return;
        }

        setTitle(R.string.prompt);
        ImageView foto = findViewById(R.id.img_foto);
        foto.setImageResource(fotoDe(prompt.getCriador()));
        recortar(foto);
        ((TextView) findViewById(R.id.txt_autor)).setText(prompt.getCriador().getNome());
        ((TextView) findViewById(R.id.txt_categoria)).setText(prompt.getCategoria().getNome());
        ((TextView) findViewById(R.id.txt_titulo)).setText(prompt.getTitulo());
        ((TextView) findViewById(R.id.txt_conteudo)).setText(prompt.getConteudo());
        listaAvaliacoes = findViewById(R.id.lista_avaliacoes);
        txtNota = findViewById(R.id.txt_nota);

        // Mecânica "Copy & Paste": copia de verdade para a área de transferência.
        findViewById(R.id.btn_copiar).setOnClickListener(v -> {
            ClipboardManager clipboard = getSystemService(ClipboardManager.class);
            clipboard.setPrimaryClip(ClipData.newPlainText("Prompt", prompt.getConteudo()));
            // O Android 13+ já mostra sozinho a confirmação de cópia.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                aviso(R.string.copiado);
            }
        });

        RatingBar estrelas = findViewById(R.id.rating);
        EditText comentario = findViewById(R.id.edt_comentario);
        findViewById(R.id.btn_avaliar).setOnClickListener(v -> {
            try {
                prompt.adicionarAvaliacao(new Avaliacao(usuario(),
                        (int) estrelas.getRating(), comentario.getText().toString().trim()));
                aviso(R.string.avaliacao_publicada);
                estrelas.setRating(0);
                comentario.setText("");
                atualizar();
            } catch (IllegalArgumentException erro) {
                // A regra da nota (1 a 5) está encapsulada na classe Avaliacao.
                aviso(erro.getMessage());
            }
        });
    }

    @Override
    protected void atualizar() {
        int total = prompt.getAvaliacoes().size();
        txtNota.setText(total == 0
                ? getString(R.string.avaliacoes)
                : getString(R.string.media_de_5, Formato.texto("%.1f", prompt.getMediaNotas()))
                        + "  ·  " + plural(total, R.string.nota_um, R.string.nota_varios));

        listaAvaliacoes.removeAllViews();
        if (total == 0) {
            adicionarLinhaIcone(listaAvaliacoes, R.drawable.ic_estrela, R.color.texto_3,
                    getString(R.string.sem_avaliacoes), getString(R.string.seja_primeiro), null, null);
            return;
        }
        for (Avaliacao a : prompt.getAvaliacoes()) {
            String texto = a.getComentario().isEmpty() ? getString(R.string.sem_comentario) : a.getComentario();
            View linha = adicionarLinhaIcone(listaAvaliacoes, R.drawable.ic_pessoa, R.color.azul_claro,
                    a.getAutor().getNome(), texto, String.valueOf(a.getNota()), null);
            comEstrela(linha);
        }
    }
}
