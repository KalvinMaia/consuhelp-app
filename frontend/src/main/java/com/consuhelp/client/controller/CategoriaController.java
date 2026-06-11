package com.consuhelp.client.controller;

import com.consuhelp.client.ConsuHelpApp;
import com.consuhelp.client.service.ApiService;
import com.consuhelp.client.util.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller da Tela de Categorização de Problemas (4.3).
 * <p>
 * Exibe o grid 2x3 de categorias pré-definidas de conflitos de consumo.
 * Ao selecionar uma categoria, envia diretamente à API com o relato categorizado.
 * </p>
 *
 * Categorias do grid:
 * Linha 1: Produto danificado | Produto não entregue | Cobrança indevida
 * Linha 2: Exigência de valor excessivo | Garantia não reconhecida | Suspeita de golpe/fraude
 */
public class CategoriaController implements Initializable {

    /** Relato inicial digitado na tela anterior (opcional). */
    private static String relatoInicial = "";

    @FXML private Label labelStatus;

    private final ApiService apiService = new ApiService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        labelStatus.setVisible(false);
    }

    // ── Handlers do Grid de Categorias ────────────────────────────────────────

    @FXML
    void onProdutoDanificado(ActionEvent event) {
        processarCategoria("Produto danificado",
                "Recebi um produto com defeito/dano e preciso de orientação sobre meus direitos.");
    }

    @FXML
    void onProdutoNaoEntregue(ActionEvent event) {
        processarCategoria("Produto não entregue",
                "Realizei uma compra e o produto não foi entregue no prazo acordado.");
    }

    @FXML
    void onCobrancaIndevida(ActionEvent event) {
        processarCategoria("Cobrança indevida",
                "Fui cobrado por um valor que não deveria ser cobrado ou por um serviço não contratado.");
    }

    @FXML
    void onExigenciaExcessiva(ActionEvent event) {
        processarCategoria("Exigência de valor excessivo",
                "Estou sendo cobrado um valor muito acima do acordado ou do praticado pelo mercado.");
    }

    @FXML
    void onGarantiaNaoReconhecida(ActionEvent event) {
        processarCategoria("Garantia não reconhecida",
                "O fornecedor se recusa a honrar a garantia legal ou contratual do produto/serviço.");
    }

    @FXML
    void onSuspeitaGolpe(ActionEvent event) {
        processarCategoria("Suspeita de golpe/fraude",
                "Acredito ter sido vítima de uma prática comercial fraudulenta ou enganosa.");
    }

    // ── Navegação ─────────────────────────────────────────────────────────────

    @FXML
    void onVoltar(ActionEvent event) {
        ConsuHelpApp.navegarPara("/fxml/dashboard.fxml");
    }

    // ── Estático para receber relato da tela anterior ─────────────────────────

    public static void setRelatoInicial(String relato) {
        relatoInicial = relato != null ? relato : "";
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void processarCategoria(String categoria, String relatoPadrao) {
        // Se o usuário já digitou algo, complementa; caso contrário usa o relato padrão
        String relato = relatoInicial.isEmpty()
                ? relatoPadrao
                : categoria + ": " + relatoInicial;

        relatoInicial = ""; // Limpa para próxima navegação

        mostrarStatus("Consultando IA para: " + categoria + "...");

        String nome = SessionManager.getInstance().getNomeUsuario();

        apiService.enviarConsulta(nome, relato)
                .thenAccept(response -> Platform.runLater(() -> {
                    ResultadoController.setConsultaResponse(response);
                    ConsuHelpApp.navegarPara("/fxml/resultado.fxml");
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            mostrarStatus("Erro ao conectar com o servidor. Tente novamente.")
                    );
                    return null;
                });
    }

    private void mostrarStatus(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.setVisible(true);
    }
}
