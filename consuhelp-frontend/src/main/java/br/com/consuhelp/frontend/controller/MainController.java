package br.com.consuhelp.frontend.controller;

import br.com.consuhelp.frontend.model.ActionPlan;
import br.com.consuhelp.frontend.model.ConsumerConflict;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final String API_URL = "http://localhost:8080/api/conflicts";

    @FXML private Button btnNewConsultation;
    @FXML private ListView<ConsumerConflict> lvHistory;
    @FXML private Label lblConnectionStatus;
    @FXML private Circle statusDot;

    @FXML private VBox paneForm;
    @FXML private TextArea txtDescription;
    @FXML private Button btnAnalyze;

    @FXML private VBox paneLoading;
    @FXML private ProgressIndicator progressIndicator;

    @FXML private ScrollPane paneResult;
    @FXML private Label lblResultTitle;
    @FXML private Label lblRecommendedPath;
    @FXML private Label lblResultSummary;
    @FXML private VBox vboxApplicableLaws;
    @FXML private VBox vboxSteps;
    @FXML private VBox vboxDocuments;
    @FXML private Label lblAdditionalNotes;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Clear result view on startup and show empty form
        showPane(paneForm);

        // Setup listener for history selection
        lvHistory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayResult(newValue);
            }
        });

        // Load existing conflicts from backend on startup
        refreshHistoryAsync();
    }

    @FXML
    private void handleNewConsultation() {
        lvHistory.getSelectionModel().clearSelection();
        txtDescription.clear();
        showPane(paneForm);
    }

    @FXML
    private void handleAnalyze() {
        String description = txtDescription.getText();
        if (description == null || description.trim().isEmpty()) {
            showAlert("Campo Vazio", "Por favor, descreva o seu conflito de consumo antes de prosseguir.", Alert.AlertType.WARNING);
            return;
        }

        showPane(paneLoading);

        // Run the API call in a background thread to prevent UI freezing
        Task<ConsumerConflict> analysisTask = new Task<>() {
            @Override
            protected ConsumerConflict call() throws Exception {
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("description", description);
                String jsonInput = objectMapper.writeValueAsString(requestBody);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                        .timeout(Duration.ofSeconds(30))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) {
                    return objectMapper.readValue(response.body(), ConsumerConflict.class);
                } else {
                    throw new RuntimeException("Erro do servidor (Status " + response.statusCode() + "): " + response.body());
                }
            }
        };

        analysisTask.setOnSucceeded(event -> {
            ConsumerConflict result = analysisTask.getValue();
            displayResult(result);
            refreshHistoryAsync(); // Reload the history list
        });

        analysisTask.setOnFailed(event -> {
            Throwable exception = analysisTask.getException();
            showPane(paneForm);
            showAlert("Erro na Análise", "Não foi possível analisar o caso. Certifique-se de que o backend está ativo.\n\nDetalhes: " + exception.getMessage(), Alert.AlertType.ERROR);
        });

        new Thread(analysisTask).start();
    }

    private void refreshHistoryAsync() {
        Task<List<ConsumerConflict>> loadTask = new Task<>() {
            @Override
            protected List<ConsumerConflict> call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), new TypeReference<List<ConsumerConflict>>() {});
                } else {
                    throw new IOException("Erro ao carregar histórico: " + response.statusCode());
                }
            }
        };

        loadTask.setOnSucceeded(event -> {
            List<ConsumerConflict> list = loadTask.getValue();
            lvHistory.setItems(FXCollections.observableArrayList(list));
            updateConnectionStatus(true);
        });

        loadTask.setOnFailed(event -> {
            updateConnectionStatus(false);
        });

        new Thread(loadTask).start();
    }

    private void displayResult(ConsumerConflict conflict) {
        ActionPlan plan = conflict.getActionPlan();
        if (plan == null) return;

        lblResultTitle.setText(plan.getTitle());
        
        // Recommended Path and Badge Color
        String recommended = plan.getRecommendedPath();
        lblRecommendedPath.setText(recommended);
        lblRecommendedPath.getStyleClass().removeAll("badge-procon", "badge-consumidor", "badge-jec", "badge-other");
        if (recommended != null) {
            String pathLower = recommended.toLowerCase();
            if (pathLower.contains("procon")) {
                lblRecommendedPath.getStyleClass().add("badge-procon");
            } else if (pathLower.contains("consumidor")) {
                lblRecommendedPath.getStyleClass().add("badge-consumidor");
            } else if (pathLower.contains("jec") || pathLower.contains("juizado")) {
                lblRecommendedPath.getStyleClass().add("badge-jec");
            } else {
                lblRecommendedPath.getStyleClass().add("badge-other");
            }
        } else {
            lblRecommendedPath.getStyleClass().add("badge-other");
        }

        lblResultSummary.setText(plan.getSummary());

        // Build Applicable Laws list dynamically
        vboxApplicableLaws.getChildren().clear();
        if (plan.getApplicableLaws() != null) {
            for (String law : plan.getApplicableLaws()) {
                VBox lawCard = new VBox();
                lawCard.getStyleClass().add("law-item");
                
                String title = law;
                String desc = "";
                int dashIndex = law.indexOf(" - ");
                if (dashIndex != -1) {
                    title = law.substring(0, dashIndex);
                    desc = law.substring(dashIndex + 3);
                }

                Label lblTitle = new Label(title);
                lblTitle.getStyleClass().add("law-title");
                lblTitle.setWrapText(true);
                
                Label lblDesc = new Label(desc);
                lblDesc.getStyleClass().add("law-desc");
                lblDesc.setWrapText(true);

                lawCard.getChildren().add(lblTitle);
                if (!desc.isEmpty()) {
                    lawCard.getChildren().add(lblDesc);
                }
                vboxApplicableLaws.getChildren().add(lawCard);
            }
        }

        // Build Steps list dynamically
        vboxSteps.getChildren().clear();
        if (plan.getSteps() != null) {
            int stepNum = 1;
            for (String step : plan.getSteps()) {
                HBox stepCard = new HBox(10);
                stepCard.getStyleClass().add("step-item");

                Label lblNum = new Label(String.valueOf(stepNum++));
                lblNum.getStyleClass().add("step-number");

                Label lblText = new Label(step);
                lblText.getStyleClass().add("step-text");
                lblText.setWrapText(true);
                HBox.setHgrow(lblText, javafx.scene.layout.Priority.ALWAYS);

                stepCard.getChildren().addAll(lblNum, lblText);
                vboxSteps.getChildren().add(stepCard);
            }
        }

        // Build Documents list dynamically
        vboxDocuments.getChildren().clear();
        if (plan.getDocumentsNeeded() != null) {
            for (String doc : plan.getDocumentsNeeded()) {
                HBox docCard = new HBox(10);
                docCard.getStyleClass().add("doc-item");

                Label lblBullet = new Label("✓");
                lblBullet.getStyleClass().add("doc-bullet");

                Label lblText = new Label(doc);
                lblText.getStyleClass().add("doc-text");
                lblText.setWrapText(true);
                HBox.setHgrow(lblText, javafx.scene.layout.Priority.ALWAYS);

                docCard.getChildren().addAll(lblBullet, lblText);
                vboxDocuments.getChildren().add(docCard);
            }
        }

        lblAdditionalNotes.setText(plan.getAdditionalNotes());

        showPane(paneResult);
    }

    private void showPane(javafx.scene.Node paneToShow) {
        paneForm.setVisible(paneToShow == paneForm);
        paneLoading.setVisible(paneToShow == paneLoading);
        paneResult.setVisible(paneToShow == paneResult);
    }

    private void updateConnectionStatus(boolean connected) {
        Platform.runLater(() -> {
            if (connected) {
                lblConnectionStatus.setText("Conectado ao Servidor");
                statusDot.setFill(Color.valueOf("#22c55e"));
            } else {
                lblConnectionStatus.setText("Servidor Desconectado");
                statusDot.setFill(Color.valueOf("#ef4444"));
            }
        });
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
