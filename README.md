# PromptPlace (app Android em Java)

App mobile do PromptPlace, um marketplace de prompts de IA: pesquisar, ver a prévia,
comprar e avaliar prompts, publicar os seus, encomendar prompts sob medida, assinar
planos e apoiar criadores. Trabalho escolar focado em **Programação Orientada a Objetos**.

É uma versão demonstrativa: não usa banco de dados nem faz cobranças reais.
O saldo é de mentira (começa em R$ 250,00 e pode ser recarregado) e os dados
ficam na memória enquanto o app está aberto.

## Visual

Mesmo visual do protótipo web: **azul (#2433E0) e branco**, fundo papel (#F5F6FB),
detalhes em amarelo (variáveis dos prompts, "+ R$ 100", "Mais escolhido"),
fontes **Bricolage Grotesque** (títulos e textos) e **JetBrains Mono** (prompts).
Os ícones são vetoriais, sem emojis.

## Idiomas

**Inglês (principal)** e **português**. O app abre em inglês; dá para trocar no seletor
**EN | PT** da tela inicial ou em **Mais > Idioma**. A escolha fica salva no celular.

- Textos das telas: `res/values/strings.xml` (inglês) e `res/values-pt/strings.xml` (português).
- Textos das classes (prompts, recibos, categorias): classe `Texto` + `enum Idioma`.

## Telas

| Tela | O que faz |
|---|---|
| Entrada | Nome e e-mail (sem senha) e seletor de idioma |
| Marketplace | Busca, ordenação (6 opções), filtro por categoria, favoritos e saldo de demonstração |
| Prompt | Prévia parcial (o resto aparece borrado), comprar, copiar, favoritar e avaliações recentes |
| Biblioteca | Abas **Comprados** e **Favoritos**, com abrir, copiar e comprar |
| Criar | Perfil do criador: números, prompts publicados e formulário para publicar (taxa de 15%) |
| Comunidade | Avaliar um prompt comprado, ranking de criadores e mural de avaliações com "Útil" |
| Mais | Conta, saldo, módulos, idioma e reiniciar a demonstração |
| Assinaturas e doações | Planos Gratuito, Pro e Equipes (mensal ou anual com 20% off), doar e assinar criador |
| Comissões | Encomenda sob medida com pagamento em custódia e 5 etapas até a entrega |
| Histórico | Extrato de compras, assinaturas, doações e comissões, com exportação em CSV |
| Modelo de negócio | Fontes de receita, simulador de receita e roadmap |

## Conceitos de POO

| Conceito | Onde aparece |
|---|---|
| Encapsulamento | Atributos `private` com getters `public`. A `Carteira` só muda o saldo por `debitar()` e `recarregar()`. `Avaliacao` só aceita 1 a 5 estrelas e comentário com 5+ letras. `Criador.publicarPrompt()` valida os campos. |
| Herança | `Criador` e `Consumidor` herdam de `Usuario`. `Compra`, `AssinaturaPlano`, `AssinaturaCriador`, `Doacao` e `Comissao` herdam de `Transacao`. Pix, Cartão e Boleto herdam de `Pagamento`. As telas herdam de `TelaBase`. |
| Polimorfismo | `Pagamento.processar()` muda em `PagamentoPix`, `PagamentoCartao` e `PagamentoBoleto` (usados para recarregar o saldo). Cada `Transacao` tem sua própria taxa em `getTaxaPlataforma()` (15%, 10%, 5% ou 100%). `exibirPerfil()` muda em `Criador` e `Consumidor`. |
| Abstração | `Usuario`, `Pagamento`, `Transacao` e `TelaBase` são `abstract`. |
| Associação | `Prompt` → `Criador`, `Avaliacao` → `Prompt`, `Compra` → `Prompt`, `Doacao` → `Criador`, `Encomenda` → `Criador`, `Consumidor` → seu perfil de `Criador`. |
| Composição | `Carteira`, `Biblioteca` e `Historico` são criados dentro do `Consumidor`. A `Encomenda` cria a sua própria `Comissao`. |
| Enum | `Categoria`, `Ordenacao` (com o comparador de cada ordem), `Plano`, `Cobranca`, `EtapaEncomenda`, `TipoTransacao`, `Situacao`, `Idioma`. |
| Exceção própria | `SaldoInsuficienteException`, lançada pela `Carteira` quando falta saldo. |

### Modificadores de acesso

- `private`: só a própria classe acessa (ex.: `saldo` em `Carteira`, `nota` em `Prompt`).
- `public`: qualquer classe acessa (ex.: `comprar()`, `publicarPrompt()`, getters).
- `protected`: a própria classe, as subclasses e o mesmo pacote (ex.: `nome` em `Usuario`,
  `valor` e `situacao` em `Transacao`, que a `Comissao` muda para "Em custódia").
- Sem modificador (pacote): `Comissao.liberar()` e `Avaliacao.alterarUteis()` só podem ser
  chamados por classes do pacote `model`, como `Encomenda` e `Consumidor`.

## Instalar no celular

1. Gere o APK (veja abaixo) e passe o arquivo para o celular (WhatsApp, Google Drive, cabo USB…).
2. Abra o arquivo no celular e toque em **Instalar**. Se aparecer o aviso de
   "fontes desconhecidas" ou do Play Protect, permita a instalação.
3. Requer Android 8.0 ou mais novo.

## Gerar o APK

```bash
./gradlew assembleDebug
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.
Também dá para abrir a pasta no **Android Studio** e clicar em ▶ Run.

## Estrutura

```
app/src/main/java/com/promptplace/
├── DadosApp.java              dados de exemplo (5 criadores, 11 prompts, 6 avaliações)
├── ConfigIdioma.java          salva e aplica o idioma
├── model/                     regras de negócio (POO, Java puro)
│   ├── Usuario.java           abstrata
│   ├── Criador.java           extends Usuario
│   ├── Consumidor.java        extends Usuario (tem Carteira, Biblioteca e Historico)
│   ├── Carteira.java          saldo de demonstração
│   ├── Biblioteca.java        comprados e favoritos
│   ├── Historico.java         extrato e CSV
│   ├── Prompt.java, Avaliacao.java, Plataforma.java, Encomenda.java, Simulador.java
│   ├── Transacao.java         abstrata: Compra, AssinaturaPlano, AssinaturaCriador, Doacao, Comissao
│   ├── Pagamento.java         abstrata: PagamentoPix, PagamentoCartao, PagamentoBoleto
│   ├── SaldoInsuficienteException.java
│   ├── Categoria, Ordenacao, Plano, Cobranca, EtapaEncomenda, TipoTransacao, Situacao, Idioma (enums)
│   └── Texto.java, Formato.java
└── telas/                     as telas (Activities), todas herdam de TelaBase
app/src/main/res/
├── layout/                    o visual de cada tela (XML)
├── values/ e values-pt/       textos em inglês e português, cores e estilos
├── drawable/                  ícones vetoriais e formas
├── drawable-nodpi/            fotos dos criadores
└── font/                      Bricolage Grotesque e JetBrains Mono (licença OFL)
```
