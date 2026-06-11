package com.consuhelp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO que representa o payload enviado pelo cliente (JavaFX) ao servidor.
 * Corresponde à estrutura de Request Body definida na proposta do projeto.
 */
public record ConsultaRequestDTO(

        @NotBlank(message = "O nome do consumidor é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String consumidorNome,

        @NotBlank(message = "O relato do problema é obrigatório.")
        @Size(min = 20, max = 3000, message = "O relato deve ter entre 20 e 3000 caracteres.")
        String relatoProblema
) {}
