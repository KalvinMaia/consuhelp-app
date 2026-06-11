package com.consuhelp.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Ponto de entrada da aplicação desktop ConsuHelp.
 * <p>
 * Inicializa o JavaFX e carrega a tela de autenticação (login) como
 * ponto de partida da navegação.
 * </p>
 */
public class ConsuHelpApp extends Application {

    /** Título exibido na barra da janela */
    public static final String TITULO_APP = "ConsuHelp";

    /** Dimensões padrão da janela */
    public static final double LARGURA = 800;
    public static final double ALTURA  = 600;

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        stage.setTitle(TITULO_APP);
        stage.setResizable(false);

        // Carrega a tela de login como ponto de entrada
        navegarPara("/fxml/login.fxml");

        stage.show();
    }

    /**
     * Utilitário estático para troca de telas (navegação entre cenas).
     *
     * @param caminhoFxml caminho do FXML dentro de resources (ex: "/fxml/dashboard.fxml")
     */
    public static void navegarPara(String caminhoFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ConsuHelpApp.class.getResource(caminhoFxml)
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, LARGURA, ALTURA);
            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            ConsuHelpApp.class.getResource("/css/estilo.css")
                    ).toExternalForm()
            );
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar tela: " + caminhoFxml, e);
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
