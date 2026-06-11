package com.consuhelp.client.controller;

import com.consuhelp.client.ConsuHelpApp;
import com.consuhelp.client.model.ConsultaResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller da Tela de Resultado.
 * <p>
 * Exibe o plano de ação estruturado gerado pela IA:
 * diagnóstico legal, canal recomendado e próximos passos.
 * </p>
 */
public class ResultadoController implements Initializable {

    /** Resposta recebida da API, passada entre controllers estaticamente. */
    private static ConsultaResponse consultaResponse;

    @FXML private Label labelStatus;
    @FXML private Label labelId;
    @FXML private TextArea areaDiagnostico;
    @FXML private TextArea areaCanal;
    @FXML private TextArea areaPassos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (consultaResponse != null) {
            preencherDados(consultaResponse);
        } else {
            labelStatus.setText("Nenhum resultado disponível.");
        }
    }

    @FXML
    void onNovaConsulta(ActionEvent event) {
        consultaResponse = null;
        ConsuHelpApp.navegarPara("/fxml/dashboard.fxml");
    }

    @FXML
    void onVerCategorias(ActionEvent event) {
        consultaResponse = null;
        ConsuHelpApp.navegarPara("/fxml/categorias.fxml");
    }

    // ─────────────────────────────────────────────────────────────────────────

    public static void setConsultaResponse(ConsultaResponse response) {
        ResultadoController.consultaResponse = response;
    }

    private void preencherDados(ConsultaResponse r) {
        labelStatus.setText("Status: " + r.getStatus());
        labelId.setText("ID: " + r.getId());

        if (r.getAnaliseIA() != null) {
            areaDiagnostico.setText(r.getAnaliseIA().getDiagnosticoLegal());
            areaCanal.setText(r.getAnaliseIA().getCanalRecomendado());

            List<String> passos = r.getAnaliseIA().getProximosPassos();
            if (passos != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < passos.size(); i++) {
                    sb.append((i + 1)).append(". ").append(passos.get(i)).append("\n");
                }
                areaPassos.setText(sb.toString());
            }
        }
    }
}
