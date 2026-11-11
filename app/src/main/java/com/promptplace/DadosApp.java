package com.promptplace;

import com.promptplace.model.Avaliacao;
import com.promptplace.model.Categoria;
import com.promptplace.model.Consumidor;
import com.promptplace.model.Criador;
import com.promptplace.model.Ordenacao;
import com.promptplace.model.Plataforma;
import com.promptplace.model.Prompt;
import com.promptplace.model.Texto;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

/**
 * Guarda os dados do app enquanto ele está aberto (sem banco de dados).
 * Os dados de exemplo são os mesmos do protótipo (index.html), em inglês e português.
 */
public class DadosApp {

    private static Plataforma plataforma;
    private static Consumidor usuario;

    // Estado dos filtros do marketplace (fica guardado ao trocar de tela).
    public static String busca = "";
    public static Categoria categoria = null; // null = todas
    public static Ordenacao ordem = Ordenacao.RELEVANTES;

    public static Plataforma getPlataforma() {
        return plataforma;
    }

    public static Consumidor getUsuario() {
        return usuario;
    }

    /** Entra no app e monta a demonstração do zero. */
    public static void entrar(String nome, String email) {
        busca = "";
        categoria = null;
        ordem = Ordenacao.RELEVANTES;
        Prompt.reiniciarNumeracao();
        plataforma = new Plataforma();
        usuario = new Consumidor(nome, email);

        // O perfil de criador de quem está usando o app (no protótipo: "Criador Demo").
        Criador voce = new Criador(nome, email, "@" + apelido(nome), null);
        usuario.setPerfilCriador(voce);

        carregarExemplos(plataforma, voce);

        // Estado inicial igual ao do protótipo: 2 compras e 2 favoritos.
        usuario.carregarCompraAnterior(plataforma.getPrompt(5), 12);
        usuario.carregarCompraAnterior(plataforma.getPrompt(2), 5);
        usuario.alternarFavorito(plataforma.getPrompt(3));
        usuario.alternarFavorito(plataforma.getPrompt(1));
    }

    /** Recomeça a demonstração com o mesmo nome e e-mail. */
    public static void reiniciar() {
        entrar(usuario.getNome(), usuario.getEmail());
    }

    public static void sair() {
        usuario = null;
        plataforma = null;
    }

    /** "Guilherme Rubim" vira "guilherme". */
    private static String apelido(String nome) {
        String primeiro = nome.trim().split("\\s+")[0].toLowerCase();
        return Normalizer.normalize(primeiro, Normalizer.Form.NFD).replaceAll("[^a-z0-9]", "");
    }

    private static List<String> modelos(String... nomes) {
        return Arrays.asList(nomes);
    }

    private static void carregarExemplos(Plataforma p, Criador voce) {
        Criador marina = new Criador("Marina Lopes", "marina@promptplace.app", "@marinalopes", "foto_marina");
        Criador rafael = new Criador("Rafael Dantas", "rafael@promptplace.app", "@rafadantas", "foto_rafael");
        Criador camila = new Criador("Camila Torres", "camila@promptplace.app", "@camitorres", null);
        Criador lucas = new Criador("Lucas Ferraz", "lucas@promptplace.app", "@lucasferraz", "foto_lucas");
        Criador beatriz = new Criador("Beatriz Nogueira", "beatriz@promptplace.app", "@beanogueira", null);
        for (Criador c : new Criador[] {marina, rafael, camila, lucas, beatriz, voce}) {
            p.cadastrarCriador(c);
        }

        // 1
        adicionar(p, new Prompt(
                new Texto("Reels Scriptwriter", "Roteirista de Reels"),
                new Texto("Three 30 to 45 second scripts with a hook, development and a call to action.",
                        "Três roteiros de 30 a 45 segundos com gancho, desenvolvimento e chamada para ação."),
                new Texto("You are a scriptwriter who specializes in short videos for Instagram and TikTok.\n\n"
                        + "Niche: [NICHE]\nVideo goal: [GOAL]\nTone of voice: [TONE]\n\n"
                        + "Deliver 3 scripts of 30 to 45 seconds. For each one:\n"
                        + "1. Hook in the first 3 seconds\n2. Development in 3 short blocks\n3. Call to action\n"
                        + "4. Caption suggestion and 5 hashtags\n\nAvoid clichés and generic phrases.",
                        "Você é um roteirista especialista em vídeos curtos para Instagram e TikTok.\n\n"
                        + "Nicho: [NICHO]\nObjetivo do vídeo: [OBJETIVO]\nTom de voz: [TOM]\n\n"
                        + "Entregue 3 roteiros de 30 a 45 segundos. Para cada um:\n"
                        + "1. Gancho nos 3 primeiros segundos\n2. Desenvolvimento em 3 blocos curtos\n3. Chamada para ação\n"
                        + "4. Sugestão de legenda e 5 hashtags\n\nEvite clichês e frases genéricas."),
                Categoria.MARKETING, modelos("ChatGPT", "Claude"), 19.90, marina), 4.8, 312, 1280);
        // 2
        adicionar(p, new Prompt(
                new Texto("Senior Code Reviewer", "Revisor de Código Sênior"),
                new Texto("A complete code review: bugs, security, performance, tests and a refactored version.",
                        "Code review completo: bugs, segurança, desempenho, testes e uma versão refatorada."),
                new Texto("Act as a senior software engineer doing a code review.\n\n"
                        + "Language: [LANGUAGE]\nProject context: [CONTEXT]\n\n"
                        + "Analyze the code below and answer in sections:\n- Bugs and security risks\n"
                        + "- Readability and naming\n- Performance\n- Missing tests\n"
                        + "- Refactored version, explaining what changed\n\n"
                        + "Be direct and rank the problems by severity.\n\n[PASTE THE CODE HERE]",
                        "Atue como engenheiro de software sênior fazendo code review.\n\n"
                        + "Linguagem: [LINGUAGEM]\nContexto do projeto: [CONTEXTO]\n\n"
                        + "Analise o código abaixo e responda em seções:\n- Bugs e riscos de segurança\n"
                        + "- Legibilidade e nomes\n- Desempenho\n- Testes que faltam\n"
                        + "- Versão refatorada, comentando o que mudou\n\n"
                        + "Seja direto e priorize os problemas por gravidade.\n\n[COLE O CÓDIGO AQUI]"),
                Categoria.PROGRAMACAO, modelos("Claude", "ChatGPT"), 24.90, rafael), 4.9, 208, 640);
        // 3
        adicionar(p, new Prompt(
                new Texto("30-Day Study Plan", "Plano de Estudos em 30 Dias"),
                new Texto("A weekly plan with goals, hands-on exercises and a final checklist for any topic.",
                        "Um plano semanal com metas, exercícios práticos e checklist final para qualquer tema."),
                new Texto("Create a 30-day study plan about [TOPIC].\n\n"
                        + "Time available per day: [MINUTES]\nCurrent level: [BEGINNER OR INTERMEDIATE]\n\n"
                        + "For each week, give the goal, the daily activities, 1 hands-on exercise and how to "
                        + "measure progress. End with a final checklist.",
                        "Crie um plano de estudos de 30 dias sobre [TEMA].\n\n"
                        + "Tempo disponível por dia: [MINUTOS]\nNível atual: [INICIANTE OU INTERMEDIÁRIO]\n\n"
                        + "Para cada semana, informe o objetivo, as atividades diárias, 1 exercício prático e como "
                        + "medir o progresso. Termine com um checklist final."),
                Categoria.ESTUDOS, modelos("Gemini", "ChatGPT"), 0, camila), 4.6, 540, 3100);
        // 4
        adicionar(p, new Prompt(
                new Texto("Studio Portraits", "Retratos de Estúdio"),
                new Texto("Image prompt for portraits with studio lighting and an editorial look.",
                        "Prompt de imagem para retratos com luz de estúdio e aparência editorial."),
                new Texto("studio portrait of [SUBJECT], soft key light, 85mm lens, shallow depth of field, "
                        + "neutral backdrop, natural skin texture, editorial look --ar 4:5 --style raw\n\n"
                        + "Variations: swap the lighting for [REMBRANDT, BUTTERFLY OR SPLIT] and the background for [COLOR].",
                        "studio portrait of [SUJEITO], soft key light, 85mm lens, shallow depth of field, "
                        + "neutral backdrop, natural skin texture, editorial look --ar 4:5 --style raw\n\n"
                        + "Variações: troque a iluminação por [REMBRANDT, BUTTERFLY OU SPLIT] e o fundo por [COR]."),
                Categoria.DESIGN, modelos("Midjourney"), 14.90, lucas), 4.7, 156, 720);
        // 5
        adicionar(p, new Prompt(
                new Texto("One-Page Competitor Analysis", "Análise de Concorrentes em 1 Página"),
                new Texto("A comparison table, three differentiation opportunities and the main risk.",
                        "Tabela comparativa, três oportunidades de diferenciação e o principal risco."),
                new Texto("You are a strategy consultant. Write a one-page competitor analysis.\n\n"
                        + "My business: [DESCRIPTION]\nCompetitors: [LIST]\n\n"
                        + "Build a table with positioning, price, strengths and weaknesses for each one. Then point "
                        + "out 3 differentiation opportunities and 1 main risk.",
                        "Você é consultor de estratégia. Faça uma análise de concorrentes em 1 página.\n\n"
                        + "Meu negócio: [DESCRIÇÃO]\nConcorrentes: [LISTA]\n\n"
                        + "Monte uma tabela com posicionamento, preço, pontos fortes e pontos fracos de cada um. "
                        + "Depois, aponte 3 oportunidades de diferenciação e 1 risco principal."),
                Categoria.NEGOCIOS, modelos("ChatGPT", "Claude"), 29.90, beatriz), 4.8, 97, 410);
        // 6
        adicionar(p, new Prompt(
                new Texto("Sales Emails That Get Replies", "E-mails de Vendas que Respondem"),
                new Texto("Three short cold emails, with subject line variations for A/B testing.",
                        "Três e-mails curtos de prospecção, com variações de assunto para teste A/B."),
                new Texto("Write 3 cold outreach emails to [AUDIENCE] offering [PRODUCT].\n\n"
                        + "Rules: up to 120 words, subject line with 6 words max, a single question as the call to "
                        + "action and no exaggerated promises.\n\nAlso deliver 2 subject line variations per email for A/B testing.",
                        "Escreva 3 e-mails de prospecção a frio para [PÚBLICO] oferecendo [PRODUTO].\n\n"
                        + "Regras: até 120 palavras, assunto com no máximo 6 palavras, uma única pergunta como chamada "
                        + "para ação e nenhuma promessa exagerada.\n\nEntregue também 2 variações de assunto por e-mail para teste A/B."),
                Categoria.MARKETING, modelos("ChatGPT", "Gemini"), 17.90, marina), 4.5, 188, 905);
        // 7
        adicionar(p, new Prompt(
                new Texto("SQL Debugger", "Depurador de SQL"),
                new Texto("Explains the error in plain language, fixes the query and suggests indexes.",
                        "Explica o erro em linguagem simples, corrige a consulta e sugere índices."),
                new Texto("Act as a SQL expert ([DATABASE]).\n\n"
                        + "I got the error: [ERROR]\nQuery: [QUERY]\nTable schema: [SCHEMA]\n\n"
                        + "Explain the cause in plain language, show the fixed query and suggest indexes or tweaks "
                        + "to improve performance.",
                        "Aja como especialista em SQL ([BANCO DE DADOS]).\n\n"
                        + "Recebi o erro: [ERRO]\nConsulta: [CONSULTA]\nEsquema das tabelas: [ESQUEMA]\n\n"
                        + "Explique a causa em linguagem simples, mostre a consulta corrigida e sugira índices ou "
                        + "ajustes para melhorar o desempenho."),
                Categoria.PROGRAMACAO, modelos("Claude", "ChatGPT"), 21.90, rafael), 4.7, 74, 260);
        // 8
        adicionar(p, new Prompt(
                new Texto("Academic Paper Summary", "Resumo de Artigos Acadêmicos"),
                new Texto("Research question, method, results, limitations and discussion questions.",
                        "Pergunta de pesquisa, método, resultados, limitações e perguntas para discussão."),
                new Texto("Summarize the academic paper below for an undergraduate student.\n\n"
                        + "Format:\n- Research question\n- Method in 3 lines\n- Main results\n- Limitations\n"
                        + "- 3 questions for class discussion\n\n[PASTE THE TEXT HERE]",
                        "Resuma o artigo acadêmico abaixo para um estudante de graduação.\n\n"
                        + "Formato:\n- Pergunta de pesquisa\n- Método em 3 linhas\n- Principais resultados\n- Limitações\n"
                        + "- 3 perguntas para discussão em aula\n\n[COLE O TEXTO AQUI]"),
                Categoria.ESTUDOS, modelos("Claude", "Gemini"), 12.90, camila), 4.4, 63, 330);
        // 9
        adicionar(p, new Prompt(
                new Texto("Visual Identity in 10 Questions", "Identidade Visual em 10 Perguntas"),
                new Texto("A guided interview that ends in a palette, typography and style direction.",
                        "Uma entrevista guiada que termina em paleta, tipografia e direção de estilo."),
                new Texto("Run a 10-question interview, one question at a time, to define the visual identity "
                        + "of my brand [BRAND].\n\nAt the end, deliver: personality keywords, a color palette with "
                        + "reasoning, typography direction, illustration style and 3 mood references.",
                        "Conduza uma entrevista de 10 perguntas, uma por vez, para definir a identidade visual da "
                        + "minha marca [MARCA].\n\nAo final, entregue: palavras-chave de personalidade, paleta de cores "
                        + "com justificativa, direção tipográfica, estilo de ilustração e 3 referências de clima visual."),
                Categoria.DESIGN, modelos("ChatGPT", "Claude"), 22.90, lucas), 4.6, 51, 190);
        // 10 (seu perfil de criador)
        adicionar(p, new Prompt(
                new Texto("Newsletter Ideas", "Ideias de Newsletter"),
                new Texto("Ten issue ideas with a title, an opening hook and a call to action.",
                        "Dez ideias de edições com título, gancho de abertura e chamada para ação."),
                new Texto("Generate 10 newsletter issue ideas about [TOPIC] for [AUDIENCE]. For each idea, give a "
                        + "title, an opening hook, 3 topics and a call to action.",
                        "Gere 10 ideias de edições de newsletter sobre [TEMA] para [PÚBLICO]. Para cada ideia traga "
                        + "título, gancho de abertura, 3 tópicos e uma chamada para ação."),
                Categoria.MARKETING, modelos("ChatGPT"), 15.90, voce), 4.5, 38, 120);
        // 11 (seu perfil de criador)
        adicionar(p, new Prompt(
                new Texto("Product Launch Checklist", "Checklist de Lançamento de Produto"),
                new Texto("Pre-launch, launch day and post-launch checklist with deadlines.",
                        "Checklist de pré-lançamento, dia do lançamento e pós-lançamento com prazos."),
                new Texto("Build a launch checklist for [PRODUCT], split into pre-launch, launch day and "
                        + "post-launch, with suggested owners and relative deadlines.",
                        "Monte um checklist de lançamento para [PRODUTO], dividido em pré-lançamento, dia do "
                        + "lançamento e pós-lançamento, com responsáveis sugeridos e prazos relativos."),
                Categoria.NEGOCIOS, modelos("Claude"), 18.90, voce), 4.6, 22, 85);

        // Avaliações de exemplo (as mais antigas entram primeiro; o mural mostra a mais recente em cima).
        avaliar(p, "Marcos T.", 6, 4, "Short emails, as promised.", "E-mails curtos, como prometido.", 9, 18);
        avaliar(p, "Fernanda L.", 4, 4, "Consistent portraits. I tweaked the light and it looked just right.",
                "Retratos consistentes. Ajustei a luz e ficou como eu queria.", 6, 13);
        avaliar(p, "Júlia R.", 3, 4, "Clear and flexible plan. I'd love a version for public exams.",
                "Plano claro e flexível. Gostaria de uma versão voltada para concursos.", 11, 9);
        avaliar(p, "Diego S.", 5, 5, "In ten minutes I put together an analysis that would take me an afternoon.",
                "Em dez minutos montei uma análise que eu levaria uma tarde para fazer.", 17, 6);
        avaliar(p, "Ana C.", 1, 5, "The hooks are really good and save hours of scripting.",
                "Os ganchos são muito bons e economizam horas de roteiro.", 15, 4);
        avaliar(p, "Paulo M.", 2, 5, "It found concurrency issues that slipped through two human reviews. Worth every cent.",
                "Achou problemas de concorrência que passaram por duas revisões humanas. Valeu cada centavo.", 24, 2);
    }

    private static void adicionar(Plataforma p, Prompt prompt, double nota, int avaliacoes, int vendas) {
        prompt.definirEstatisticas(nota, avaliacoes, vendas);
        p.adicionarPrompt(prompt);
    }

    private static void avaliar(Plataforma p, String nome, int idPrompt, int estrelas,
                                String ingles, String portugues, int uteis, int diasAtras) {
        p.registrarAvaliacao(new Avaliacao(nome, p.getPrompt(idPrompt), estrelas,
                new Texto(ingles, portugues), uteis, diasAtras));
    }
}
