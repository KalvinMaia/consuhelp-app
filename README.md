# ConsuHelp

> **Central Inteligente de Assistência e Consultoria Jurídica ao Consumidor**

## Descrição

O ConsuHelp é uma aplicação cliente-servidor que atua como uma central de consultoria jurídica para consumidores. O usuário descreve seu problema em linguagem natural e o sistema — integrado com a API do Google Gemini — processa e retorna instantaneamente um plano de ação estruturado com o melhor caminho administrativo ou judicial para resolução do conflito (PROCON, Consumidor.gov.br, JEC, etc.), com embasamento no Código de Defesa do Consumidor (CDC – Lei 8.078/1990).

---

## Descrição do Problema

No Brasil, os conflitos de consumo (atrasos na entrega, falhas de qualidade, cobranças indevidas, práticas de venda casada e multas abusivas) são recorrentes e geram expressivo volume de demandas. Contudo, o cidadão comum frequentemente enfrenta barreiras para fazer valer seus direitos:
1. **Dificuldade de Interpretação**: A redação técnica do Código de Defesa do Consumidor (CDC) dificulta a identificação clara de quais artigos foram infringidos em um caso concreto.
2. **Desorientação Processual**: O consumidor desconhece a distinção e as esferas de atuação dos diferentes canais de reclamação e resolução, tais como o **PROCON** municipal, a plataforma digital federada **Consumidor.gov.br** ou a esfera judicial de pequenas causas via **Juizado Especial Cível (JEC)**.
3. **Falta de Preparação**: Há dúvidas frequentes sobre quais documentos são indispensáveis para formalizar uma reclamação e quais os passos sequenciais corretos.

O **ConsuHelp** resolve essa lacuna ao atuar como uma central inteligente de assistência jurídica: traduz o relato em linguagem natural do usuário em um plano de ação fundamentado no CDC, listando o canal adequado, as leis aplicáveis, os passos práticos e a documentação necessária.

---

## Público-Alvo

- **Consumidores Finais (Cidadãos)**: Pessoas físicas que buscam orientação rápida, didática e imediata sobre como proceder em caso de conflitos de consumo sem a necessidade inicial de contratar assessoria técnica.
- **Consultores e Órgãos de Apoio**: Auxiliares jurídicos, associações de moradores ou centrais comunitárias que necessitem de uma ferramenta automatizada para triagem rápida e recomendação de caminhos para resolução de conflitos de consumo.

---

## Escopo da Solução

### Em Escopo (In-Scope)
- **Análise em Linguagem Natural**: Processamento do relato livre do usuário sobre o incidente com a compra ou serviço.
- **Fundamentação Jurídica CDC**: Mapeamento e apresentação de artigos pertinentes do CDC (Lei 8.078/1990) com explicações resumidas do direito.
- **Roteamento Inteligente (Canais)**: Recomendação clara do canal mais rápido para solução (Consumidor.gov.br para resoluções administrativas rápidas; PROCON para práticas abusivas gerais; JEC para cancelamentos contratuais judiciais e danos morais).
- **Checklist de Documentos e Ações**: Geração dinâmica de passos de ação recomendados e rol de documentos probatórios necessários.
- **Histórico Lateral**: Registro e consulta das análises em memória para auditoria rápida.

### Fora de Escopo (Out-of-Scope)
- **Peticionamento Eletrônico**: O app não realiza o ajuizamento direto de ações em tribunais ou o registro direto nos canais governamentais.
- **Representação Legal**: O plano gerado é consultivo e de orientação prática, não configurando representação ou patrocínio de advogado.
- **Assinatura Digital**: Não há fluxo de assinatura ou geração de procurações e declarações automáticas.

---

## Arquitetura e Estrutura do Projeto

```
consuhelp/
├── consuhelp-backend/
│   ├── pom.xml
│   └── src/main/...
│       ├── BackendApplication.java
│       ├── controller/ConflictController.java
│       ├── service/ConflictService.java, GeminiService.java
│       ├── repository/ConflictRepository.java
│       └── domain/ConsumerConflict.java, ActionPlan.java
└── consuhelp-frontend/
    ├── pom.xml
    └── src/main/...
        ├── App.java, AppLauncher.java
        ├── controller/MainController.java
        ├── model/ConsumerConflict.java, ActionPlan.java
        └── resources/...
            ├── main.fxml
            └── styles.css (Visual Premium)
```

---

## Detalhes da Implementação

### 1. Backend Spring Boot 3+ ("consuhelp-backend")

- **Integração com Google Gemini**: Usando a biblioteca **Spring AI 1.1.8** com suporte nativo ao modelo `gemini-1.5-flash`.
- **Fallback Inteligente (Mock Mode)**: Caso não haja chave de API definida (valor default `mock-mode`), ou em caso de erros de conexão/limites de quota, o backend ativa de forma transparente um sistema local de processamento inteligente. Ele analisa palavras-chave do conflito em tempo real (como "geladeira", "voo", "negativação", etc.) e gera um plano de ação dinâmico e contextualizado de acordo com o Código de Defesa do Consumidor (CDC).
- **Repositório em Memória**: Implementado usando `ConcurrentHashMap` thread-safe.
- **APIs REST expostas**:
  - `POST /api/conflicts`: Recebe e analisa o conflito em tempo real.
  - `GET /api/conflicts`: Retorna a lista de consultas ordenadas por data de criação.
  - `DELETE /api/conflicts/{id}`: Exclui consultas específicas.

### 2. Frontend Desktop JavaFX (`consuhelp-frontend`)

  - **Fundo**: Gradiente escuro e profundo (`#0a0b10` para `#131127`).
  - **Componentes**: Efeito glassmorphism (fundos translúcidos e bordas em tons neon).
  - **Logotipo**: Neon violeta com brilho dinâmico.
  - **Badges de Canal Recomendados**: Coloridos de forma inteligente (Consumidor.gov.br em verde-esmeralda, PROCON em ouro/âmbar, JEC em vermelho suave).
  - **Customização de Layout**: Scrollbars e ListViews customizadas para encaixar no tema escuro.
- **Processamento Assíncrono**: As requisições HTTP são processadas em uma thread secundária via `javafx.concurrent.Task`. Isso busca impedir que a UI trave enquanto o Gemini analisa o conflito, exibindo uma animação de carregamento e ativando os painéis conforme o resultado é retornado.
- **Histórico Lateral**: Um menu esquerdo com histórico atualizável que permite alternar instantaneamente entre consultas passadas e uma nova consulta.

---

## Verificação e Testes Realizados

### 1. Compilação e Empacotamento
Ambos os projetos foram compilados e empacotados utilizando o **JDK 26**:
- **Backend**: Compilado com sucesso (`BUILD SUCCESS`)
- **Frontend**: Compilado com sucesso (`BUILD SUCCESS`)

### 2. Verificação de Endpoint HTTP (PowerShell)
Uma requisição teste foi enviada para o backend:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/conflicts" -Method Post -Body '{"description":"Comprei uma geladeira que veio com defeito e o vendedor recusa a troca."}' -ContentType "application/json"
```
**Resposta do Servidor (Sucesso):**
O backend retornou o plano perfeitamente estruturado em JSON com base no CDC:
```json
{
  "id": "a0dd2ab4-5bea-4b5d-9a35-67335f5e55ad",
  "description": "Comprei uma geladeira que veio com defeito e o vendedor recusa a troca.",
  "createdAt": "2026-07-06T23:27:20.666",
  "actionPlan": {
    "title": "Vício/Defeito em Produto de Consumo",
    "summary": "O relato indica que o produto adquirido apresenta vícios de qualidade... o fornecedor possui o prazo legal de até 30 dias para sanar o defeito...",
    "recommendedPath": "Consumidor.gov.br",
    "applicableLaws": [
      "Artigo 18, § 1º do CDC - Estabelece a responsabilidade solidária...",
      "Artigo 26, II do CDC - Define o prazo de garantia legal..."
    ],
    "steps": [
      "Passo 1: Entrar em contato com o vendedor...",
      "Passo 2: Aguardar o prazo legal de 30 dias...",
      "Passo 3: Registrar reclamação formal no portal Consumidor.gov.br..."
    ],
    "documentsNeeded": [
      "Nota Fiscal de compra do produto",
      "Cópia da Ordem de Serviço da assistência técnica...",
      "Protocolos de atendimento..."
    ],
    "additionalNotes": "Nota importante: Para produtos considerados essenciais (como geladeira)..."
  }
}
```

---

## Como Executar o Sistema Localmente

Para interagir com o ConsulHelp diretamente, utilize os comandos abaixo apontando para o seu **JDK 26** (exemplo de diretório possível: `C:\Program Files\Java\jdk-26.0.1`):

### 1. Iniciar o Backend
```powershell
# No diretório consuhelp-backend:
& "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" -jar target\consuhelp-backend-1.0.0.jar
```

### 2. Iniciar o Frontend JavaFX
```powershell
# No diretório consuhelp-frontend:
$env:JAVA_HOME="C:\Program Files\Java\jdk-26.0.1"
mvn javafx:run
```
