package com.consuhelp.dto;

import java.util.List;

/**
 * DTO que representa o bloco "analiseIA" contido no Response Body.
 * Estrutura gerada pela integração com o Google Gemini e retornada ao cliente.
 */
public record AnaliseIADTO(

        /** Diagnóstico legal com base no CDC (Código de Defesa do Consumidor). */
        String diagnosticoLegal,

        /** Canal de resolução recomendado (PROCON, Consumidor.gov.br, JEC etc.). */
        String canalRecomendado,

        /** Lista ordenada de passos práticos que o consumidor deve seguir. */
        List<String> proximosPassos
) {}
