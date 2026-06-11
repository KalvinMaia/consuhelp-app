package com.consuhelp.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO que representa o payload completo de resposta enviado ao cliente.
 * Corresponde exatamente à estrutura de Response Body definida na proposta.
 *
 * Exemplo:
 * {
 *   "id": "8f3b9c42-7eed-4d83-9912-19bc305e24a7",
 *   "dataHora": "2026-05-18T22:40:00Z",
 *   "status": "CONCLUIDO",
 *   "analiseIA": { ... }
 * }
 */
public record ConsultaResponseDTO(

        UUID id,
        Instant dataHora,
        String status,
        AnaliseIADTO analiseIA
) {}
