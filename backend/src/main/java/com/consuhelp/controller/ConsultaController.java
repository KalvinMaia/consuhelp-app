package com.consuhelp.controller;

import com.consuhelp.dto.ConsultaRequestDTO;
import com.consuhelp.dto.ConsultaResponseDTO;
import com.consuhelp.service.ConsultaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST do ConsuHelp.
 * <p>
 * Expõe os endpoints HTTP que o cliente JavaFX consome.
 * Todas as rotas são prefixadas por {@code /api/consultas}.
 * </p>
 *
 * <pre>
 * POST   /api/consultas         → processa novo relato e retorna análise jurídica
 * GET    /api/consultas         → lista todas as consultas em memória
 * GET    /api/consultas/{id}    → retorna uma consulta específica pelo UUID
 * </pre>
 */
@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private static final Logger log = LoggerFactory.getLogger(ConsultaController.class);

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    /**
     * Recebe o relato do consumidor, processa com IA e retorna o plano de ação.
     *
     * @param request payload JSON com nome e relato do consumidor
     * @return 201 CREATED + {@link ConsultaResponseDTO} com análise completa
     */
    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> processarConsulta(
            @Valid @RequestBody ConsultaRequestDTO request) {

        log.info("Recebida requisição POST /api/consultas para: {}", request.consumidorNome());
        ConsultaResponseDTO response = consultaService.processarConsulta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas as consultas armazenadas em memória.
     *
     * @return 200 OK + lista de {@link ConsultaResponseDTO}
     */
    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> listarConsultas() {
        List<ConsultaResponseDTO> consultas = consultaService.listarTodas();
        return ResponseEntity.ok(consultas);
    }

    /**
     * Busca uma consulta específica pelo UUID.
     *
     * @param id UUID da consulta
     * @return 200 OK + {@link ConsultaResponseDTO}, ou 404 NOT FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarConsulta(@PathVariable UUID id) {
        return consultaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
