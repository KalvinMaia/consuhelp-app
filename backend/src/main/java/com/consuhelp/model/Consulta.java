package com.consuhelp.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Entidade de domínio que representa uma consulta jurídica realizada por um consumidor.
 * Persistida no repositório em memória (HashMap keyed by UUID).
 */
public class Consulta {

    private UUID id;
    private Instant dataHora;
    private String status;

    // Dados do consumidor
    private String consumidorNome;
    private String relatoProblema;

    // Resultado da análise de IA
    private String diagnosticoLegal;
    private String canalRecomendado;
    private List<String> proximosPassos;

    // ── Construtores ──────────────────────────────────────────────────────────

    public Consulta() {}

    public Consulta(String consumidorNome, String relatoProblema) {
        this.id = UUID.randomUUID();
        this.dataHora = Instant.now();
        this.status = StatusConsulta.PROCESSANDO.name();
        this.consumidorNome = consumidorNome;
        this.relatoProblema = relatoProblema;
    }

    // ── Getters e Setters ─────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Instant getDataHora() { return dataHora; }
    public void setDataHora(Instant dataHora) { this.dataHora = dataHora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConsumidorNome() { return consumidorNome; }
    public void setConsumidorNome(String consumidorNome) { this.consumidorNome = consumidorNome; }

    public String getRelatoProblema() { return relatoProblema; }
    public void setRelatoProblema(String relatoProblema) { this.relatoProblema = relatoProblema; }

    public String getDiagnosticoLegal() { return diagnosticoLegal; }
    public void setDiagnosticoLegal(String diagnosticoLegal) { this.diagnosticoLegal = diagnosticoLegal; }

    public String getCanalRecomendado() { return canalRecomendado; }
    public void setCanalRecomendado(String canalRecomendado) { this.canalRecomendado = canalRecomendado; }

    public List<String> getProximosPassos() { return proximosPassos; }
    public void setProximosPassos(List<String> proximosPassos) { this.proximosPassos = proximosPassos; }

    // ── toString ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Consulta{id=" + id + ", consumidor='" + consumidorNome + "', status='" + status + "'}";
    }
}
