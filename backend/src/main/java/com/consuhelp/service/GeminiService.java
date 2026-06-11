package com.consuhelp.service;

import com.consuhelp.dto.AnaliseIADTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Serviço responsável pela orquestração da comunicação com a API do Google Gemini.
 * <p>
 * Constrói o prompt jurídico especializado, envia ao modelo de linguagem e
 * desserializa a resposta estruturada em JSON para o DTO de análise.
 * </p>
 */
@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public GeminiService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Envia o relato do consumidor ao Google Gemini e retorna a análise jurídica estruturada.
     *
     * @param nomeConsumidor nome do consumidor para personalização
     * @param relato         descrição do problema em linguagem natural
     * @return {@link AnaliseIADTO} com diagnóstico, canal recomendado e próximos passos
     */
    public AnaliseIADTO analisarRelato(String nomeConsumidor, String relato) {
        String prompt = construirPrompt(nomeConsumidor, relato);

        log.debug("Enviando relato ao Gemini para o consumidor: {}", nomeConsumidor);

        String respostaJson = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        log.debug("Resposta bruta do Gemini: {}", respostaJson);

        return parseResposta(respostaJson);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos privados
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Constrói o prompt estruturado com instruções jurídicas e formato de saída esperado.
     */
    private String construirPrompt(String nomeConsumidor, String relato) {
        return """
                Você é um assistente jurídico especializado no Código de Defesa do Consumidor (CDC) \
                brasileiro (Lei 8.078/1990). Sua função é analisar problemas de consumo relatados \
                pelos cidadãos e fornecer orientações claras, precisas e acessíveis.

                Consumidor: %s
                Relato: %s

                Com base no relato acima, retorne EXCLUSIVAMENTE um objeto JSON válido (sem markdown, \
                sem blocos de código) com a seguinte estrutura:
                {
                  "diagnosticoLegal": "Descrição do enquadramento jurídico no CDC, citando o artigo aplicável.",
                  "canalRecomendado": "Canal mais indicado para resolução: PROCON, Consumidor.gov.br, JEC ou outro.",
                  "proximosPassos": [
                    "Passo 1 concreto que o consumidor deve tomar",
                    "Passo 2...",
                    "Passo 3..."
                  ]
                }

                Regras:
                - Seja objetivo e use linguagem acessível ao cidadão comum.
                - Cite o artigo do CDC quando aplicável.
                - Liste entre 3 e 5 próximos passos práticos.
                - Não invente fatos. Se o relato for insuficiente, oriente o consumidor a fornecer mais detalhes.
                - Retorne SOMENTE o JSON, sem texto adicional.
                """.formatted(nomeConsumidor, relato);
    }

    /**
     * Desserializa a resposta JSON do Gemini para o DTO de análise.
     * Remove eventuais marcações de bloco de código que o modelo possa ter inserido.
     */
    @SuppressWarnings("unchecked")
    private AnaliseIADTO parseResposta(String json) {
        try {
            // Remove possíveis marcações ```json ... ``` que o modelo pode retornar
            String jsonLimpo = json
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            Map<String, Object> mapa = objectMapper.readValue(jsonLimpo,
                    new TypeReference<Map<String, Object>>() {});

            String diagnostico = (String) mapa.getOrDefault("diagnosticoLegal",
                    "Não foi possível determinar o diagnóstico legal.");
            String canal = (String) mapa.getOrDefault("canalRecomendado",
                    "Consulte o PROCON local para orientação.");
            List<String> passos = (List<String>) mapa.getOrDefault("proximosPassos",
                    List.of("Entre em contato com o PROCON da sua cidade para mais informações."));

            return new AnaliseIADTO(diagnostico, canal, passos);

        } catch (Exception e) {
            log.error("Erro ao interpretar resposta do Gemini: {}", e.getMessage());
            return new AnaliseIADTO(
                    "Erro ao processar análise jurídica. Tente novamente.",
                    "PROCON local",
                    List.of("Tente reformular o relato do problema com mais detalhes.")
            );
        }
    }
}
