package com.consuhelp.client.controller;

import com.consuhelp.client.ConsuHelpApp;
import com.consuhelp.client.service.ApiService;
import com.consuhelp.client.util.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller da Tela de Consulta Inicial / Dashboard (4.2).
 * <p>
 * Exibe o painel principal com barra de busca em linguagem natural.
 * Ao submeter, navega para a tela de categorização ou envia diretamente à API.
 * </p>
 */
public class DashboardController implements Initializable {

    @FXML private Label labelBoasVindas;
    @FXML private TextField campoBusca;
    @FXML private Button botaoBuscar;
    @FXML private Label labelStatus;

    private final ApiService apiService = new ApiService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String nome = SessionManager.getInstance().getNomeUsuario();
        if (nome != null) {
            labelBoasVindas.setText("Olá, " + nome + "!");
        }
        labelStatus.setVisible(false);
    }

    /**
     * Acionado pela barra de busca (botão lupa ou Enter).
     * Navega para a tela de categorização ou envia diretamente se o relato for longo.
     */
    @FXML
    void onBuscar(ActionEvent event) {
        String relato = campoBusca.getText().trim();

        if (relato.isEmpty()) {
            mostrarStatus("Digite seu problema antes de buscar.");
            return;
        }

        if (relato.length() < 20) {
            // Relato curto → redirecionar para categorização
            CategoriaController.setRelatoInicial(relato);
            ConsuHelpApp.navegarPara("/fxml/categorias.fxml");
        } else {
            // Relato detalhado → enviar direto à API
            enviarConsulta(relato);
        }
    }

    /**
     * Acionado pelo ícone de categorias no menu lateral.
     */
    @FXML
    void onAbrirCategorias(ActionEvent event) {
        ConsuHelpApp.navegarPara("/fxml/categorias.fxml");
    }

    /**
     * Acionado pelo botão de logout.
     */
    @FXML
    void onLogout(ActionEvent event) {
        SessionManager.getInstance().encerrarSessao();
        ConsuHelpApp.navegarPara("/fxml/login.fxml");
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void enviarConsulta(String relato) {
        botaoBuscar.setDisable(true);
        mostrarStatus("Analisando seu caso com IA... Aguarde.");

        String nome = SessionManager.getInstance().getNomeUsuario();

        apiService.enviarConsulta(nome, relato)
                .thenAccept(response -> Platform.runLater(() -> {
                    // Navega para tela de resultado
                    ResultadoController.setConsultaResponse(response);
                    ConsuHelpApp.navegarPara("/fxml/resultado.fxml");
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        botaoBuscar.setDisable(false);
                        mostrarStatus("Erro ao conectar com o servidor. Verifique sua conexão.");
                    });
                    return null;
                });
    }

    private void mostrarStatus(String mensagem) {
        labelStatus.setText(mensagem);
        labelStatus.setVisible(true);
    }
}
