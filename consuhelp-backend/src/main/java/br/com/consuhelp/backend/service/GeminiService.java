package br.com.consuhelp.backend.service;

import br.com.consuhelp.backend.domain.ActionPlan;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GeminiService {
    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Autowired(required = false)
    private ChatModel chatModel;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.ai.google.genai.api-key:mock-mode}")
    private String apiKey;

    public ActionPlan analyzeConflict(String description) {
        if ("mock-mode".equals(apiKey) || "mock".equalsIgnoreCase(apiKey) || chatModel == null) {
            log.info("Gemini API Key is not set or mock mode is active. Using local mock simulation.");
            return generateMockPlan(description);
        }

        try {
            String systemPrompt = """
                Você é um assistente jurídico especialista no Código de Defesa do Consumidor brasileiro (CDC - Lei 8.078/1990).
                Sua tarefa é analisar o relato de conflito de consumo enviado pelo usuário e gerar um plano de ação estruturado em formato JSON.
                
                A resposta deve ser APENAS um objeto JSON válido, sem markdown (não envolva em blocos de código com ```json ... ```), obedecendo estritamente à seguinte estrutura:
                {
                  "title": "Título curto e descritivo do conflito",
                  "summary": "Resumo jurídico detalhado fundamentando quem tem direito e o porquê com base no CDC",
                  "recommendedPath": "PROCON ou Consumidor.gov.br ou Juizado Especial Cível (JEC) ou Outro",
                  "applicableLaws": [
                    "Artigo XX do CDC - [Breve descrição e aplicação]"
                  ],
                  "steps": [
                    "Passo 1: [Instrução clara]",
                    "Passo 2: [Instrução clara]"
                  ],
                  "documentsNeeded": [
                    "Documento 1",
                    "Documento 2"
                  ],
                  "additionalNotes": "Conselhos adicionais, prazos de decadência ou prescrição aplicáveis"
                }
                
                Responda em Português do Brasil de forma clara e profissional. Não adicione nenhum texto explicativo fora do JSON.
                """;

            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(systemPrompt),
                    new UserMessage(description)
            ));

            log.info("Sending request to Gemini API...");
            String rawResponse = chatModel.call(prompt).getResult().getOutput().getText();
            log.info("Received response from Gemini API");

            // Clean up code block markdown formatting if Gemini included it
            String cleanedResponse = rawResponse.trim();
            if (cleanedResponse.startsWith("```json")) {
                cleanedResponse = cleanedResponse.substring(7);
            } else if (cleanedResponse.startsWith("```")) {
                cleanedResponse = cleanedResponse.substring(3);
            }
            if (cleanedResponse.endsWith("```")) {
                cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
            }
            cleanedResponse = cleanedResponse.trim();

            return objectMapper.readValue(cleanedResponse, ActionPlan.class);
        } catch (Exception e) {
            log.warn("Failed to generate plan using Gemini API. Falling back to mock generator. Error: {}", e.getMessage());
            return generateMockPlan(description);
        }
    }

    private ActionPlan generateMockPlan(String description) {
        String descLower = description.toLowerCase();

        if (descLower.contains("abusiv") || descLower.contains("cláusula") || descLower.contains("clausula") ||
                descLower.contains("contrat") || descLower.contains("multa") || descLower.contains("juros") ||
                descLower.contains("exorbitante") || descLower.contains("venda casada")) {
            return ActionPlan.builder()
                    .title("Práticas Abusivas e Cláusulas Contratuais Nulas")
                    .summary("A imposição de cláusulas contratuais que estabelecem obrigações abusivas, coloquem o consumidor em desvantagem exagerada ou imponham multas rescisórias exorbitantes é considerada nula de pleno direito, conforme o Artigo 51 do Código de Defesa do Consumidor (CDC). Ademais, condutas como exigência de vantagem manifestamente excessiva ou venda casada configuram práticas abusivas vedadas pelo Artigo 39 do CDC, ensejando a revisão contratual e denúncia nos órgãos de proteção.")
                    .recommendedPath("PROCON")
                    .applicableLaws(Arrays.asList(
                            "Artigo 51 do CDC - Declara nulas de pleno direito as cláusulas contratuais abusivas que atenuem a responsabilidade do fornecedor, limitem direitos ou gerem desvantagem exagerada.",
                            "Artigo 39 do CDC - Proíbe expressamente práticas abusivas no mercado de consumo, como a venda casada, a elevação de preços sem justa causa ou exigência de vantagem excessiva.",
                            "Artigo 6º, V do CDC - Assegura o direito básico do consumidor à modificação ou revisão de cláusulas contratuais que estabeleçam prestações desproporcionais."
                    ))
                    .steps(Arrays.asList(
                            "Passo 1: Reunir a cópia completa do contrato assinado, proposta ou Termos de Serviço aceitos digitalmente, destacando os pontos abusivos.",
                            "Passo 2: Notificar formalmente o fornecedor por escrito (e-mail, chat ou carta com AR) contestando a cláusula/cobrança exorbitante e propondo uma negociação ou rescisão sem penalidades.",
                            "Passo 3: Se o fornecedor recusar acordo, registrar uma reclamação formal no PROCON de sua localidade para abertura de processo administrativo. Para anular judicialmente a multa e exigir devolução de valores, ingressar com ação no Juizado Especial Cível (JEC)."
                    ))
                    .documentsNeeded(Arrays.asList(
                            "Cópia completa do contrato, termo de adesão ou regulamento do serviço",
                            "Comprovantes de faturas, cobranças, e-mails ou telas que comprovem a imposição dos termos abusivos",
                            "Protocolos de atendimento e e-mails com as tentativas de contestação junto à empresa",
                            "Documentos de identificação pessoal (RG, CPF e comprovante de residência)"
                    ))
                    .additionalNotes("Nota de Cuidado: O CDC prevê que a nulidade de uma cláusula abusiva específica não invalida o contrato inteiro, desde que seja possível restabelecer o equilíbrio das obrigações restantes (Artigo 51, § 2º do CDC).")
                    .build();
        }

        if (descLower.contains("entreg") || descLower.contains("receb") || descLower.contains("atraso") ||
                descLower.contains("não chegou") || descLower.contains("chegar") || descLower.contains("chegou") ||
                descLower.contains("prazo")) {
            return ActionPlan.builder()
                    .title("Atraso na Entrega / Não Entrega de Produto")
                    .summary("O descumprimento do prazo de entrega ou a não entrega de produto adquirido configura descumprimento da oferta por parte do fornecedor. Conforme o Artigo 35 do Código de Defesa do Consumidor (CDC), diante do descumprimento da oferta, o consumidor pode livremente optar por: exigir o cumprimento forçado da obrigação, aceitar outro produto ou serviço equivalente, ou rescindir o contrato com direito à restituição imediata da quantia paga, monetariamente atualizada, sem prejuízo de eventuais perdas e danos.")
                    .recommendedPath("Consumidor.gov.br")
                    .applicableLaws(Arrays.asList(
                            "Artigo 35 do CDC - Assegura a livre escolha do consumidor em exigir a entrega, aceitar produto equivalente ou cancelar com reembolso imediato em caso de não cumprimento da oferta.",
                            "Artigo 30 do CDC - Estabelece que toda informação ou publicidade vincula o fornecedor, obrigando-o a cumprir os termos oferecidos (incluindo o prazo de entrega prometido)."
                    ))
                    .steps(Arrays.asList(
                            "Passo 1: Entrar em contato imediatamente com o SAC do fornecedor exigindo informações sobre o paradeiro da mercadoria e registrando a reclamação com protocolo.",
                            "Passo 2: Estipular um prazo final razoável para a entrega. Caso não seja atendido, registrar uma reclamação na plataforma Consumidor.gov.br ou no PROCON da sua cidade anexando a nota/pedido.",
                            "Passo 3: Se optar pelo cancelamento da compra e devolução do dinheiro, e o fornecedor recusar ou demorar para estornar, ajuizar ação no Juizado Especial Cível (JEC) pleiteando o reembolso e indenização por eventuais danos materiais ou morais decorrentes do atraso injustificado."
                    ))
                    .documentsNeeded(Arrays.asList(
                            "Comprovante de compra (Nota Fiscal ou e-mail de confirmação do pedido contendo a descrição do item)",
                            "Comprovante de pagamento (Extrato do cartão de crédito, comprovante do PIX ou boleto pago)",
                            "Print ou documento mostrando a data limite prometida para a entrega no ato da compra",
                            "Protocolos de atendimento e cópias de e-mails/mensagens enviados cobrando a entrega"
                    ))
                    .additionalNotes("Nota Importante: O atraso desproporcional na entrega de itens essenciais ou destinados a eventos específicos (como presente de aniversário ou casamento) pode gerar danos morais indenizáveis devido ao transtorno e à frustração causados.")
                    .build();
        }

        if (descLower.contains("geladeira") || descLower.contains("celular") || descLower.contains("produto") ||
                descLower.contains("tv") || descLower.contains("televisão") || descLower.contains("defeito") ||
                descLower.contains("quebrou") || descLower.contains("estragou")) {
            return ActionPlan.builder()
                    .title("Vício/Defeito em Produto de Consumo")
                    .summary("O relato indica que o produto adquirido apresenta vícios de qualidade que o tornam inadequado ao consumo. Sob a égide do Código de Defesa do Consumidor (CDC), o fornecedor possui o prazo legal de até 30 dias para sanar o defeito em assistência técnica. Decorrido esse prazo sem solução, o consumidor pode exigir, alternativamente e à sua escolha, a substituição do produto por outro da mesma espécie, a restituição imediata da quantia paga ou o abatimento proporcional do preço.")
                    .recommendedPath("Consumidor.gov.br")
                    .applicableLaws(Arrays.asList(
                            "Artigo 18, § 1º do CDC - Estabelece a responsabilidade solidária dos fornecedores por vícios de qualidade e o prazo de 30 dias para reparo.",
                            "Artigo 26, II do CDC - Define o prazo de garantia legal de 90 dias para reclamar de vícios em produtos duráveis, contados a partir da entrega ou da descoberta do vício oculto."
                    ))
                    .steps(Arrays.asList(
                            "Passo 1: Entrar em contato com o vendedor ou com a assistência técnica autorizada do fabricante para abrir uma ordem de serviço e obter o protocolo de atendimento.",
                            "Passo 2: Aguardar o prazo legal de 30 dias corridos para a realização do conserto do produto.",
                            "Passo 3: Caso o produto não seja reparado em 30 dias, registre uma reclamação formal no portal Consumidor.gov.br ou no PROCON local, anexando a Ordem de Serviço, exigindo a devolução total do dinheiro ou um produto novo."
                    ))
                    .documentsNeeded(Arrays.asList(
                            "Nota Fiscal de compra do produto",
                            "Cópia da Ordem de Serviço da assistência técnica constando a data de entrada do produto",
                            "Protocolos de atendimento de contatos com o SAC do fabricante ou lojista"
                    ))
                    .additionalNotes("Nota importante: Para produtos considerados essenciais (como geladeira, fogão, telefone celular, entre outros), o consumidor não precisa aguardar o prazo de 30 dias para reparo, podendo exigir a troca imediata ou devolução da quantia com base no Artigo 18, § 3º do CDC.")
                    .build();
        } else if (descLower.contains("inscrição") || descLower.contains("spc") || descLower.contains("serasa") ||
                descLower.contains("negativação") || descLower.contains("nome sujo") || descLower.contains("indevida") ||
                descLower.contains("dívida") || descLower.contains("banco") || descLower.contains("cartão") ||
                descLower.contains("cobrança")) {
            return ActionPlan.builder()
                    .title("Negativação Indevida e Falha em Serviços Financeiros")
                    .summary("O registro do nome do consumidor em cadastros de inadimplentes (SPC/Serasa) decorrente de dívida inexistente, já paga ou sem prévia notificação por escrito configura ato ilícito e prática comercial abusiva. Sob a ótica do CDC, a responsabilidade das instituições financeiras é objetiva (Súmula 297/STJ), gerando o dever de dar baixa na anotação imediatamente e reparar o dano moral provocado, que segundo a jurisprudência é presumido (in re ipsa).")
                    .recommendedPath("Juizado Especial Cível (JEC)")
                    .applicableLaws(Arrays.asList(
                            "Artigo 42, parágrafo único do CDC - Garante o direito à repetição de indébito (devolução em dobro) em caso de cobrança indevida paga.",
                            "Artigo 43, § 2º do CDC - Exige a comunicação prévia por escrito ao consumidor antes da efetivação da inscrição de seu nome em cadastros de restrição ao crédito.",
                            "Artigo 6º, VI do CDC - Assegura a efetiva prevenção e reparação de danos patrimoniais e morais sofridos pelo consumidor."
                    ))
                    .steps(Arrays.asList(
                            "Passo 1: Emitir o comprovante oficial de negativação (com data, hora e origem do apontamento) diretamente no site do SPC, Serasa ou Boa Vista SCPC.",
                            "Passo 2: Entrar em contato com o SAC ou Ouvidoria da empresa credora solicitando a imediata exclusão do registro e o cancelamento da cobrança, anotando o protocolo.",
                            "Passo 3: Se a restrição não for retirada em até 5 dias úteis, dirigir-se ao Juizado Especial Cível da sua comarca para propor Ação de Inexistência de Débito c/c Obrigação de Fazer (liminar para retirar o nome) e Indenização por Danos Morais."
                    ))
                    .documentsNeeded(Arrays.asList(
                            "Extrato oficial da negativação ativa (emitido nos últimos 30 dias)",
                            "Comprovantes de pagamento da fatura ou documento que demonstre a inexistência da relação jurídica",
                            "Protocolo de atendimento das tentativas de solução consensual",
                            "Documentos pessoais (RG, CPF e comprovante de residência atualizado)"
                    ))
                    .additionalNotes("Nota de cautela: Conforme a Súmula 385 do Superior Tribunal de Justiça (STJ), caso o consumidor já possua outras inscrições legítimas preexistentes no cadastro de inadimplentes, não caberá indenização por danos morais, restando apenas o direito ao cancelamento da nova inscrição indevida.")
                    .build();
        } else if (descLower.contains("voo") || descLower.contains("atraso") || descLower.contains("cancelamento") ||
                descLower.contains("mala") || descLower.contains("bagagem") || descLower.contains("viagem") ||
                descLower.contains("avião") || descLower.contains("aérea")) {
            return ActionPlan.builder()
                    .title("Cancelamento/Atraso de Voo e Extravio de Bagagem")
                    .summary("O transporte aéreo é uma prestação de serviço regida pelo CDC e regulamentada pela ANAC. Atrasos superiores a 4 horas ou cancelamento unilateral de voos geram responsabilidade objetiva à companhia aérea por falha na prestação do serviço. O consumidor tem direito à assistência material gradativa (comunicação, alimentação e hospedagem) e a opções de reacomodação, execução do serviço por outra modalidade ou reembolso integral da passagem.")
                    .recommendedPath("Consumidor.gov.br")
                    .applicableLaws(Arrays.asList(
                            "Artigo 14 do CDC - Define que o fornecedor responde, independentemente da existência de culpa, pela reparação dos danos causados aos consumidores por defeitos relativos à prestação dos serviços.",
                            "Resolução nº 400/2016 da ANAC - Determina deveres específicos de informação, assistência material e reembolso em casos de atraso e cancelamento de voos."
                    ))
                    .steps(Arrays.asList(
                            "Passo 1: Solicitar imediatamente no balcão da companhia aérea a Declaração de Atraso ou Cancelamento de voo assinada.",
                            "Passo 2: Exigir a assistência material obrigatória: facilidade de comunicação após 1h de atraso; alimentação após 2h; e hospedagem (com transporte de ida/volta) após 4h.",
                            "Passo 3: Registrar reclamação em Consumidor.gov.br contra a companhia aérea detalhando as falhas de serviço e assistência e pleiteando um acordo indenizatório.",
                            "Passo 4: Se o conflito não for sanado de forma amigável, propor uma ação indenizatória de danos morais e materiais no Juizado Especial Cível mais próximo."
                    ))
                    .documentsNeeded(Arrays.asList(
                            "Passagem aérea emitida e cartão de embarque original",
                            "Declaração escrita emitida pela empresa sobre o motivo do atraso/cancelamento",
                            "Recibos e notas fiscais de gastos com alimentação, táxis e hospedagem resultantes do incidente",
                            "Relatório de Irregularidade de Bagagem (PIR) - obrigatório para casos de extravio de malas"
                    ))
                    .additionalNotes("Prazos Importantes: O prazo de prescrição para requerer indenizações decorrentes de falha de serviços de transporte aéreo nacional é de 5 anos (Artigo 27 do CDC), contudo, a ANAC recomenda que as reclamações de bagagem ocorram de imediato ou em até 7 dias da entrega.")
                    .build();
        } else {
            return ActionPlan.builder()
                    .title("Análise Geral de Relação de Consumo")
                    .summary("Com base nos fatos relatados, identifica-se uma típica relação de consumo caracterizada pela vulnerabilidade técnica e econômica do consumidor em face do fornecedor. O CDC veda expressamente condutas abusivas, falta de informação clara e falha de qualidade ou segurança nos serviços e produtos prestados no mercado, garantindo a proteção e defesa integral dos direitos do consumidor lesado.")
                    .recommendedPath("PROCON")
                    .applicableLaws(Arrays.asList(
                            "Artigo 4º, I do CDC - Reconhece explicitamente a vulnerabilidade do consumidor no mercado de consumo.",
                            "Artigo 6º, VIII do CDC - Permite a facilitação da defesa dos direitos do consumidor, inclusive com a inversão do ônus da prova no processo cível.",
                            "Artigo 39 do CDC - Lista as práticas abusivas vedadas aos fornecedores de produtos e serviços."
                    ))
                    .steps(Arrays.asList(
                            "Passo 1: Entrar em contato com o canal de atendimento oficial do fornecedor, registrando a reclamação e anotando o número de protocolo gerado e o prazo prometido para resposta.",
                            "Passo 2: Caso não obtenha resposta satisfatória no prazo dado, registre uma queixa administrativa formal junto ao PROCON de seu município ou por meio da plataforma digital integrada.",
                            "Passo 3: Havendo recusa ou descumprimento das orientações do PROCON, busque assessoria jurídica ou dirija-se ao Juizado Especial Cível para ingressar com ação judicial sem necessidade de advogado (para causas de até 20 salários mínimos)."
                    ))
                    .documentsNeeded(Arrays.asList(
                            "Documento oficial de identificação (RG e CPF) e comprovante de endereço",
                            "Comprovante do produto/serviço (nota fiscal, contrato, fatura, publicidade ou anúncio do produto)",
                            "Histórico de conversas (e-mails, prints de WhatsApp) e números de protocolos de atendimento registrados"
                    ))
                    .additionalNotes("Dica do CDC: Em compras realizadas fora do estabelecimento comercial (Internet ou telefone), o consumidor tem o direito de se arrepender e desistir da compra em até 7 dias corridos da assinatura do contrato ou do recebimento do produto, com devolução imediata de todos os valores pagos (Artigo 49 do CDC).")
                    .build();
        }
    }
}
