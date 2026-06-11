package com.consuhelp.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Representa a análise jurídica retornada pela API do ConsuHelp.
 * Mapeado a partir do campo "analiseIA" no JSON de resposta.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnaliseIA {

    private String diagnosticoLegal;
    private String canalRecomendado;
    private List<String> proximosPassos;

    public AnaliseIA() {}

    public String getDiagnosticoLegal() { return diagnosticoLegal; }
    public void setDiagnosticoLegal(String diagnosticoLegal) { this.diagnosticoLegal = diagnosticoLegal; }

    public String getCanalRecomendado() { return canalRecomendado; }
    public void setCanalRecomendado(String canalRecomendado) { this.canalRecomendado = canalRecomendado; }

    public List<String> getProximosPassos() { return proximosPassos; }
    public void setProximosPassos(List<String> proximosPassos) { this.proximosPassos = proximosPassos; }
}
