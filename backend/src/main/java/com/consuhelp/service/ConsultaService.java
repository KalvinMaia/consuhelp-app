package com.consuhelp.service;

import com.consuhelp.dto.AnaliseIADTO;
import com.consuhelp.dto.ConsultaRequestDTO;
import com.consuhelp.dto.ConsultaResponseDTO;
import com.consuhelp.model.Consulta;
import com.consuhelp.model.StatusConsulta;
import com.consuhelp.repository.ConsultaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Camada de serviço principal do ConsuHelp.
 * <p>
 * Orquestra o fluxo completo: recebe o relato do consumidor, delega ao
 * {@link GeminiService} para análise jurídica por IA, persiste no repositório
 * em memória e retorna o resultado formatado ao controller.
 * </p>
 */
@Service
public class ConsultaService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);

    private final GeminiService geminiService;
    private final ConsultaRepository repository;

    public ConsultaService(GeminiService geminiService, ConsultaRepository repository) {
        this.geminiService = geminiService;
        this.repository = repository;
    }

    /**
     * Processa um novo relato de problema de consumo.
     * <p>
     * Fluxo:
     * 1. Cria entidade {@link Consulta} com status PROCESSANDO.
     * 2. Persiste no repositório em memória.
     * 3. Delega análise ao Gemini.
     * 4. Atualiza a entidade com o resultado e status CONCLUIDO.
     * 5. Retorna o DTO de resposta ao controller.
     * </p>
     *
     * @param request DTO contendo nome do consumidor e relato do problema
     * @return {@link ConsultaResponseDTO} com análise completa
     */
    public ConsultaResponseDTO processarConsulta(ConsultaRequestDTO request) {
        // 1. Criar e persistir a consulta
        Consulta consulta = new Consulta(request.consumidorNome(), request.relatoProblema());
        repository.salvar(consulta);

        log.info("Nova consulta registrada: id={}, consumidor={}",
                consulta.getId(), consulta.getConsumidorNome());

        try {
            // 2. Obter análise jurídica do Gemini
            AnaliseIADTO analise = geminiService.analisarRelato(
                    request.consumidorNome(),
                    request.relatoProblema()
            );

            // 3. Atualizar entidade com resultado
            consulta.setDiagnosticoLegal(analise.diagnosticoLegal());
            consulta.setCanalRecomendado(analise.canalRecomendado());
            consulta.setProximosPassos(analise.proximosPassos());
            consulta.setStatus(StatusConsulta.CONCLUIDO.name());
            repository.salvar(consulta);

            log.info("Consulta concluída: id={}", consulta.getId());

            return toResponseDTO(consulta);

        } catch (Exception e) {
            // 4. Registrar falha no repositório
            consulta.setStatus(StatusConsulta.ERRO.name());
            consulta.setDiagnosticoLegal("Erro interno ao processar a análise. Tente novamente.");
            consulta.setCanalRecomendado("PROCON local");
            consulta.setProximosPassos(List.of("Tente novamente em alguns instantes."));
            repository.salvar(consulta);

            log.error("Erro ao processar consulta {}: {}", consulta.getId(), e.getMessage());

            return toResponseDTO(consulta);
        }
    }

    /**
     * Retorna uma consulta específica pelo seu ID.
     *
     * @param id UUID da consulta
     * @return Optional com o DTO de resposta, ou vazio se não encontrado
     */
    public Optional<ConsultaResponseDTO> buscarPorId(UUID id) {
        return repository.buscarPorId(id).map(this::toResponseDTO);
    }

    /**
     * Lista todas as consultas armazenadas em memória.
     *
     * @return lista de DTOs de resposta
     */
    public List<ConsultaResponseDTO> listarTodas() {
        return repository.listarTodas()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mapper interno
    // ─────────────────────────────────────────────────────────────────────────

    private ConsultaResponseDTO toResponseDTO(Consulta c) {
        AnaliseIADTO analise = new AnaliseIADTO(
                c.getDiagnosticoLegal(),
                c.getCanalRecomendado(),
                c.getProximosPassos()
        );
        return new ConsultaResponseDTO(c.getId(), c.getDataHora(), c.getStatus(), analise);
    }
}
