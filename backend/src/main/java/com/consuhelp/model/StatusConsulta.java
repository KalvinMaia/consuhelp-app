package com.consuhelp.model;

/**
 * Enum que representa os estados possíveis de uma consulta no sistema.
 */
public enum StatusConsulta {

    /** Consulta recebida e sendo processada pelo backend/IA. */
    PROCESSANDO,

    /** Análise concluída com sucesso e resposta disponível para o cliente. */
    CONCLUIDO,

    /** Falha durante o processamento (erro na IA, payload inválido etc.). */
    ERRO
}
