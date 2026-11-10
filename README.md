# PromptPlace (app Android em Java)

App mobile demonstrativo do PromptPlace: criadores vendem assinaturas do seu
"cofre" de prompts de IA, e o usuário encontra, copia e usa os prompts em segundos.
Trabalho escolar focado em **Programação Orientada a Objetos**.
Não usa banco de dados nem faz pagamentos reais: os dados ficam na memória enquanto o app está aberto.

## Visual

Tema escuro com uma única cor principal, **azul royal (#2445FB)**, inspirado em apps
como Revolut e Apple Fitness: cartões arredondados, cartão de destaque em
gradiente, fotos dos criadores e barra de navegação flutuante. Sem emojis:
todos os ícones são vetoriais.

## Idiomas

O app tem **inglês (principal)** e **português (secundário)**. Abre em inglês por padrão;
dá para trocar no seletor **EN | PT** da tela de login ou em **Profile > Language**.
A escolha fica salva no celular.

- Textos das telas: `res/values/strings.xml` (inglês) e `res/values-pt/strings.xml` (português).
- Textos das classes (prompts, bio, recibo do pagamento): classe `Texto`, que guarda as
  duas versões, e o `enum Idioma`, que diz qual está ativa.
- `ConfigIdioma` salva a escolha e aplica em todas as telas.

## Instalar no celular

1. Gere o APK (veja abaixo) e passe o arquivo para o celular (WhatsApp, Google Drive, cabo USB…).
2. Abra o arquivo no celular e toque em **Instalar**.
   Se aparecer um aviso de "fontes desconhecidas", permita a instalação para o app que abriu o arquivo.
3. Requer Android 8.0 ou mais novo.

## Gerar o APK

```bash
./gradlew assembleDebug
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.
Também dá para abrir a pasta do projeto no **Android Studio** e clicar em ▶ Run.

## Telas

| Tela | O que faz |
|---|---|
| Login | Nome e e-mail (sem senha, é demonstração) |
| Feed | Lista os criadores, com filtro Imagem / Texto / Código |
| Perfil do criador | **Teste cego** (1 prompt grátis sorteado) e botão de assinar |
| Pagamento | Pix, Cartão ou Boleto, com a divisão **70% criador / 30% plataforma** |
| Biblioteca | Todos os prompts desbloqueados |
| Prompt | Texto completo, botão **Copiar prompt** e avaliações (1 a 5 estrelas) |
| Meu perfil | Assinaturas e painel financeiro (saldos e receita) |

## Conceitos de POO

| Conceito | Onde aparece |
|---|---|
| Encapsulamento | Atributos `private` com getters `public`. `Criador.setPrecoAssinatura` bloqueia valores abaixo de R$ 10. `Avaliacao` só aceita notas de 1 a 5. |
| Herança | `Criador` e `Consumidor` usam `extends Usuario`. As telas usam `extends TelaBase`. |
| Polimorfismo | `PagamentoPix`, `PagamentoCartao` e `PagamentoBoleto` sobrescrevem `processar()` com `@Override`. `Criador` e `Consumidor` sobrescrevem `exibirPerfil()`. |
| Abstração | `Usuario`, `Pagamento` e `TelaBase` são `abstract`. |
| Associação | `Prompt` → `Criador`, `Prompt` → `Avaliacao`, `Assinatura` → `Consumidor` + `Criador` + `Pagamento`. |
| Composição | A `Biblioteca` é criada dentro do construtor de `Consumidor` e só existe junto com ele. |
| Enum | `Categoria` (Imagem, Texto, Código) e `Idioma` (inglês, português). |

### Modificadores de acesso

- `private`: só a própria classe acessa (ex.: `id` em `Usuario`, `saldo` em `Criador`).
- `public`: qualquer classe acessa (ex.: getters, `publicarPrompt`, `assinar`).
- `protected`: a própria classe, as subclasses e o mesmo pacote (ex.: `nome` e `email` em `Usuario`).
  Como as telas ficam no pacote `telas` e as classes no pacote `model`,
  o `Criador` acessa `nome` direto, mas as telas precisam chamar `getNome()`.

## Estrutura

```
app/src/main/java/com/promptplace/
├── DadosApp.java            dados do app (criadores e prompts de exemplo, nos 2 idiomas)
├── ConfigIdioma.java        salva e aplica o idioma escolhido
├── model/                   regras de negócio (POO)
│   ├── Usuario.java         abstrata
│   ├── Criador.java         extends Usuario
│   ├── Consumidor.java      extends Usuario (contém a Biblioteca)
│   ├── Biblioteca.java      composição
│   ├── Prompt.java
│   ├── Categoria.java       enum: IMAGEM, TEXTO, CODIGO
│   ├── Avaliacao.java
│   ├── Assinatura.java
│   ├── Plataforma.java
│   ├── Pagamento.java       abstrata
│   ├── PagamentoPix.java
│   ├── PagamentoCartao.java
│   ├── PagamentoBoleto.java
│   ├── Idioma.java          enum: INGLES (principal), PORTUGUES
│   ├── Texto.java           um texto em inglês e português
│   └── Formato.java         números no padrão do idioma (14.90 / 14,90)
└── telas/                   as telas do app (Activities)
    ├── TelaBase.java        abstrata, base das telas
    ├── LoginActivity.java
    ├── FeedActivity.java
    ├── CriadorActivity.java
    ├── PagamentoActivity.java
    ├── BibliotecaActivity.java
    ├── PromptActivity.java
    └── PerfilActivity.java
app/src/main/res/layout/         o visual de cada tela (XML)
app/src/main/res/values/         textos em inglês, cores e estilos
app/src/main/res/values-pt/      textos em português
app/src/main/res/drawable/       ícones vetoriais, cores e formas
app/src/main/res/drawable-nodpi/ fotos dos criadores (avatar e capa)
```
