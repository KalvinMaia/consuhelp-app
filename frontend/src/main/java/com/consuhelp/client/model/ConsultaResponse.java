package com.consuhelp.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Representa o objeto de resposta completo retornado pela API REST do ConsuHelp.
 *
 * Exemplo de JSON recebido:
 * {
 *   "id": "8f3b9c42-...",
 *   "dataHora": "2026-05-18T22:40:00Z",
 *   "status": "CONCLUIDO",
 *   "analiseIA": { ... }
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultaResponse {

    private String id;
    private String dataHora;
    private String status;
    private AnaliseIA analiseIA;

    public ConsultaResponse() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDataHora() { return dataHora; }
    public void setDataHora(String dataHora) { this.dataHora = dataHora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public AnaliseIA getAnaliseIA() { return analiseIA; }
    public void setAnaliseIA(AnaliseIA analiseIA) { this.analiseIA = analiseIA; }
}
