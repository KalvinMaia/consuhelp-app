# ConsuHelp

> **Central Inteligente de Assistência e Consultoria Jurídica ao Consumidor**

## Descrição

O ConsuHelp é uma aplicação cliente-servidor que atua como uma central de consultoria jurídica para consumidores. O usuário descreve seu problema em linguagem natural e o sistema — integrado com a API do Google Gemini — processa e retorna instantaneamente um plano de ação estruturado com o melhor caminho administrativo ou judicial para resolução do conflito (PROCON, Consumidor.gov.br, JEC, etc.), com embasamento no Código de Defesa do Consumidor (CDC – Lei 8.078/1990).

---

## Arquitetura

```
[ Cliente: Interface Desktop JavaFX ]
        │                   ▲
        │ HTTP POST (JSON)   │ HTTP 201 (JSON)
        ▼                   │
[ Servidor: API Spring Boot 3+ ] ◄──► [ API Externa: Google Gemini ]
```

- **Frontend**: Aplicação desktop JavaFX (Java 17+)
- **Backend**: API REST Spring Boot 3+ (Java 17+)
- **IA**: Google Gemini via Spring AI
- **Persistência**: Repositório em memória (HashMap por UUID)

---

## Estrutura do Projeto

```
consuhelp/
├── pom.xml                          ← POM raiz (multi-módulo)
│
├── backend/                         ← Módulo Spring Boot
│   ├── pom.xml
│   └── src/main/java/com/consuhelp/
│       ├── ConsuHelpApplication.java
│       ├── controller/
│       │   └── ConsultaController.java   ← POST /api/consultas
│       ├── service/
│       │   ├── ConsultaService.java      ← Lógica de negócio
│       │   └── GeminiService.java        ← Integração Gemini
│       ├── repository/
│       │   └── ConsultaRepository.java   ← HashMap<UUID, Consulta>
│       ├── model/
│       │   ├── Consulta.java
│       │   └── StatusConsulta.java
│       ├── dto/
│       │   ├── ConsultaRequestDTO.java
│       │   ├── ConsultaResponseDTO.java
│       │   └── AnaliseIADTO.java
│       └── config/
│           ├── CorsConfig.java
│           └── GlobalExceptionHandler.java
│
└── frontend/                        ← Módulo JavaFX
    ├── pom.xml
    └── src/main/
        ├── java/com/consuhelp/client/
        │   ├── ConsuHelpApp.java          ← Entry point JavaFX
        │   ├── controller/
        │   │   ├── LoginController.java   ← Tela 4.1
        │   │   ├── DashboardController.java ← Tela 4.2
        │   │   ├── CategoriaController.java ← Tela 4.3
        │   │   └── ResultadoController.java
        │   ├── model/
        │   │   ├── ConsultaResponse.java
        │   │   └── AnaliseIA.java
        │   ├── service/
        │   │   └── ApiService.java        ← HttpClient → Spring Boot
        │   └── util/
        │       └── SessionManager.java
        └── resources/
            ├── fxml/
            │   ├── login.fxml
            │   ├── dashboard.fxml
            │   ├── categorias.fxml
            │   └── resultado.fxml
            └── css/
                └── estilo.css
```

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java JDK | 17+ |
| Maven | 3.8+ |


---

## Tecnologias

**Backend**
- Java 17+
- Spring Boot 3.3
- Spring AI (Vertex AI Gemini)
- Bean Validation (Jakarta)
- Jackson

**Frontend**
- Java 17+
- JavaFX 21
- Java HttpClient (nativo)
- Jackson

---

## Justificativa Arquitetural

A escolha do modelo cliente-servidor baseia-se em dois pilares:

1. **Segurança**: a chave da API do Google Gemini permanece protegida nas variáveis de ambiente do servidor, nunca exposta no código cliente distribuído.

2. **Escalabilidade**: as regras de triagem do prompt e a formatação jurídica da resposta podem ser atualizadas no backend sem que os usuários finais precisem reinstalar o cliente desktop.
