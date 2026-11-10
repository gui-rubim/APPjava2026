package com.promptplace;

import com.promptplace.model.Avaliacao;
import com.promptplace.model.Categoria;
import com.promptplace.model.Consumidor;
import com.promptplace.model.Criador;
import com.promptplace.model.Plataforma;
import com.promptplace.model.Prompt;
import com.promptplace.model.Texto;

/**
 * Guarda os dados do app enquanto ele está aberto (sem banco de dados).
 * Todas as telas pegam a plataforma e o usuário logado daqui.
 * Os textos de exemplo estão em inglês e português (classe Texto).
 */
public class DadosApp {

    private static Plataforma plataforma;
    private static Consumidor usuario;

    public static Plataforma getPlataforma() {
        if (plataforma == null) {
            plataforma = new Plataforma();
            carregarExemplos(plataforma);
        }
        return plataforma;
    }

    public static Consumidor getUsuario() {
        return usuario;
    }

    public static void entrar(String nome, String email) {
        usuario = new Consumidor(nome, email);
    }

    public static void sair() {
        usuario = null;
    }

    private static void carregarExemplos(Plataforma p) {
        // ---------------- Lucas Arte (Imagem) ----------------
        Criador lucas = new Criador("Lucas Arte", "lucas@email.com",
                new Texto("Art director. I use these prompts in my work for brands.",
                        "Diretor de arte. Uso estes prompts nos meus trabalhos para marcas."),
                "foto_lucas", Categoria.IMAGEM, 19.90);
        lucas.publicarPrompt(
                new Texto("Cinematic portrait", "Retrato cinematográfico"),
                new Texto("Portrait of [person], golden hour light, 85mm lens, shallow depth of field, "
                        + "cinematic style, warm tones --ar 3:4",
                        "Retrato de [pessoa], luz de fim de tarde, lente 85mm, profundidade de campo rasa, "
                        + "estilo cinematográfico, cores quentes --ar 3:4"));
        lucas.publicarPrompt(
                new Texto("Minimalist logo", "Logo minimalista"),
                new Texto("Minimalist logo for [brand], flat vector icon, two colors max, "
                        + "white background, no text, flat design style",
                        "Logo minimalista para [marca], ícone vetorial plano, no máximo duas cores, "
                        + "fundo branco, sem texto, estilo flat design"));
        lucas.publicarPrompt(
                new Texto("Studio product shot", "Produto em estúdio"),
                new Texto("Product photo of [product] in a studio, seamless [color] background, soft side "
                        + "lighting, subtle floor reflection, high resolution, premium e-commerce style",
                        "Foto de produto de [produto] em estúdio, fundo infinito [cor], iluminação suave lateral, "
                        + "reflexo sutil no chão, alta resolução, estilo e-commerce premium"));

        // ---------------- Marina Lopes (Texto) ----------------
        Criador marina = new Criador("Marina Lopes", "marina@email.com",
                new Texto("Copywriter and social media manager. Prompts tested with 50+ clients.",
                        "Copywriter e social media. Prompts testados com mais de 50 clientes."),
                "foto_marina", Categoria.TEXTO, 14.90);
        marina.publicarPrompt(
                new Texto("Reels scriptwriter", "Roteirista de Reels"),
                new Texto("You are a short-form video scriptwriter. Write 3 scripts of 30 seconds for "
                        + "[business], aiming to [goal], in a [tone] tone. Each script opens with a hook in "
                        + "the first 3 seconds and ends with a call to action.",
                        "Você é roteirista de vídeos curtos. Crie 3 roteiros de 30 segundos para [negócio], "
                        + "com o objetivo de [objetivo], em tom [tom]. Cada roteiro começa com um gancho nos "
                        + "3 primeiros segundos e termina com uma chamada para ação."));
        marina.publicarPrompt(
                new Texto("Sales email", "E-mail de vendas"),
                new Texto("Write a short sales email for [product]. Use a curiosity-driven subject line, "
                        + "highlight 3 practical benefits and end with an offer that expires in [deadline].",
                        "Escreva um e-mail de vendas curto para [produto]. Use um assunto curioso, destaque "
                        + "3 benefícios práticos e termine com uma oferta com prazo de [prazo]."));
        marina.publicarPrompt(
                new Texto("Meeting summary", "Resumo de reunião"),
                new Texto("Summarize the notes below as bullet points: 1) decisions made, 2) tasks with "
                        + "owner and deadline, 3) next steps. Be direct.\n\n[paste your notes here]",
                        "Resuma as anotações abaixo em tópicos: 1) decisões tomadas, 2) tarefas com responsável "
                        + "e prazo, 3) próximos passos. Seja direto.\n\n[cole as anotações aqui]"));

        // ---------------- Rafael Dev (Código) ----------------
        Criador rafael = new Criador("Rafael Dev", "rafael@email.com",
                new Texto("Software engineer. The prompts I use every day in Claude Code and Cursor.",
                        "Engenheiro de software. Os prompts que uso todo dia no Claude Code e no Cursor."),
                "foto_rafael", Categoria.CODIGO, 29.90);
        rafael.publicarPrompt(
                new Texto("Code reviewer", "Revisor de código"),
                new Texto("Review the code below looking for bugs, security issues and readability "
                        + "problems. For each issue, explain why and show the fix.\n\n[paste the code here]",
                        "Revise o código abaixo procurando bugs, problemas de segurança e pontos de legibilidade. "
                        + "Para cada problema, explique o motivo e mostre a correção.\n\n[cole o código aqui]"));
        rafael.publicarPrompt(
                new Texto("Test generator", "Gerador de testes"),
                new Texto("Write unit tests in [language] for the function below. Cover the normal case, "
                        + "edge values and invalid inputs. Explain what each test checks.",
                        "Escreva testes unitários em [linguagem] para a função abaixo. Cubra o caso normal, "
                        + "valores limite e entradas inválidas. Explique o que cada teste verifica."));
        rafael.publicarPrompt(
                new Texto("Error explainer", "Explicador de erros"),
                new Texto("Explain this error as if I were a beginner and show me step by step how to fix "
                        + "it. Also tell me how to avoid it next time.\n\nError: [paste the error here]",
                        "Explique este erro como se eu fosse iniciante e mostre o passo a passo para resolver. "
                        + "Diga também como evitar que ele aconteça de novo.\n\nErro: [cole o erro aqui]"));

        p.cadastrarCriador(lucas);
        p.cadastrarCriador(marina);
        p.cadastrarCriador(rafael);

        // Algumas avaliações de outros usuários, para o app não começar vazio.
        Consumidor bruno = new Consumidor("Bruno", "bruno@email.com");
        Consumidor carla = new Consumidor("Carla", "carla@email.com");
        avaliar(marina.getCofre().get(0), bruno, 5,
                new Texto("My Reels reach tripled!", "Meus Reels triplicaram de alcance!"));
        avaliar(marina.getCofre().get(0), carla, 4,
                new Texto("Really good, I only tweaked the tone.", "Muito bom, só ajustei o tom."));
        avaliar(rafael.getCofre().get(0), carla, 5,
                new Texto("It found a bug I had missed.", "Achou um bug que eu não tinha visto."));
        avaliar(lucas.getCofre().get(1), bruno, 4,
                new Texto("Great starting point for logos.", "Ótimo ponto de partida para logos."));
    }

    private static void avaliar(Prompt prompt, Consumidor autor, int nota, Texto comentario) {
        prompt.adicionarAvaliacao(new Avaliacao(autor, nota, comentario));
    }
}
