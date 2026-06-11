package com.consuhelp.client.service;

import com.consuhelp.client.model.ConsultaResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Serviço de comunicação HTTP com a API REST do ConsuHelp (Spring Boot).
 * <p>
 * Utiliza o {@link java.net.http.HttpClient} nativo do Java 11+ para realizar
 * requisições assíncronas ao servidor, desacoplando completamente a camada de
 * UI da lógica de rede.
 * </p>
 */
public class ApiService {

    /** URL base da API. Configurável via variável de ambiente CONSUHELP_API_URL. */
    private static final String BASE_URL = System.getenv()
            .getOrDefault("CONSUHELP_API_URL", "http://localhost:8080");

    private static final String ENDPOINT_CONSULTAS = BASE_URL + "/api/consultas";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Envia o relato do consumidor ao servidor de forma assíncrona.
     *
     * @param nomeConsumidor nome do usuário logado
     * @param relatoProblema descrição do problema em linguagem natural
     * @return {@link CompletableFuture} com o {@link ConsultaResponse} desserializado
     */
    public CompletableFuture<ConsultaResponse> enviarConsulta(
            String nomeConsumidor, String relatoProblema) {

        String payload = construirPayload(nomeConsumidor, relatoProblema);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT_CONSULTAS))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(60))  // Gemini pode demorar
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        return httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 201 || response.statusCode() == 200) {
                        return deserializar(response.body());
                    }
                    throw new RuntimeException(
                            "Erro na API: status " + response.statusCode() +
                            " | Corpo: " + response.body()
                    );
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos privados
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Constrói o payload JSON manualmente para evitar dependência extra no cliente.
     */
    private String construirPayload(String nome, String relato) {
        try {
            // Usa ObjectMapper para garantir escape correto de caracteres especiais
            return objectMapper.writeValueAsString(
                    new java.util.HashMap<String, String>() {{
                        put("consumidorNome", nome);
                        put("relatoProblema", relato);
                    }}
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar payload", e);
        }
    }

    private ConsultaResponse deserializar(String json) {
        try {
            return objectMapper.readValue(json, ConsultaResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar resposta da API: " + e.getMessage(), e);
        }
    }
}
