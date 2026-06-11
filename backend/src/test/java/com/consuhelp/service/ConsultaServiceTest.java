package com.consuhelp.service;

import com.consuhelp.dto.AnaliseIADTO;
import com.consuhelp.dto.ConsultaRequestDTO;
import com.consuhelp.dto.ConsultaResponseDTO;
import com.consuhelp.repository.ConsultaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultaService - Testes Unitários")
class ConsultaServiceTest {

    @Mock
    private GeminiService geminiService;

    @Mock
    private ConsultaRepository repository;

    @InjectMocks
    private ConsultaService consultaService;

    private ConsultaRequestDTO requestValido;
    private AnaliseIADTO analiseEsperada;

    @BeforeEach
    void setUp() {
        requestValido = new ConsultaRequestDTO(
                "Kalvin Maia",
                "Comprei um smartphone com a tela trincada de fábrica e a loja se recusou a trocar."
        );

        analiseEsperada = new AnaliseIADTO(
                "Vício aparente do produto (Art. 26 do CDC). Prazo de 90 dias para reclamar.",
                "Consumidor.gov.br ou PROCON local.",
                List.of("Reunir Nota Fiscal.", "Fotografar o dano.", "Registrar reclamação formal.")
        );
    }

    @Test
    @DisplayName("Deve processar consulta e retornar análise com status CONCLUIDO")
    void processarConsulta_comRelatorValido_deveRetornarAnalise() {
        when(geminiService.analisarRelato(anyString(), anyString())).thenReturn(analiseEsperada);
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        ConsultaResponseDTO response = consultaService.processarConsulta(requestValido);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("CONCLUIDO");
        assertThat(response.id()).isNotNull();
        assertThat(response.analiseIA().diagnosticoLegal()).contains("Art. 26 do CDC");
        assertThat(response.analiseIA().proximosPassos()).hasSize(3);

        verify(geminiService, times(1)).analisarRelato(
                eq("Kalvin Maia"), anyString()
        );
    }

    @Test
    @DisplayName("Deve retornar status ERRO quando o Gemini lançar exceção")
    void processarConsulta_quandoGeminiLancaExcecao_deveRetornarErro() {
        when(geminiService.analisarRelato(anyString(), anyString()))
                .thenThrow(new RuntimeException("Timeout na API do Gemini"));
        when(repository.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

        ConsultaResponseDTO response = consultaService.processarConsulta(requestValido);

        assertThat(response.status()).isEqualTo("ERRO");
        assertThat(response.analiseIA().diagnosticoLegal()).contains("Erro interno");
    }

    @Test
    @DisplayName("Deve listar todas as consultas do repositório")
    void listarTodas_deveRetornarListaVazia_quandoSemConsultas() {
        when(repository.listarTodas()).thenReturn(List.of());

        var lista = consultaService.listarTodas();

        assertThat(lista).isEmpty();
    }
}
