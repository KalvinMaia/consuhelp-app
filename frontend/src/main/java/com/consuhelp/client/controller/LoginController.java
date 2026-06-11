package com.consuhelp.client.controller;

import com.consuhelp.client.ConsuHelpApp;
import com.consuhelp.client.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller da Tela de Autenticação (4.1).
 * <p>
 * Controla o painel de login com campos de usuário e senha.
 * Autenticação local por simplificação (sem backend de usuários neste estágio).
 * </p>
 */
public class LoginController {

    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelErro;

    /**
     * Acionado pelo botão "Entrar".
     * Valida credenciais e navega para o dashboard.
     */
    @FXML
    void onEntrar(ActionEvent event) {
        String login = campoLogin.getText().trim();
        String senha = campoSenha.getText().trim();

        labelErro.setVisible(false);

        if (login.isEmpty() || senha.isEmpty()) {
            exibirErro("Preencha todos os campos.");
            return;
        }

        // Validação local simplificada (demo)
        // Em produção: enviar ao backend /api/auth para JWT
        if (autenticarLocal(login, senha)) {
            SessionManager.getInstance().iniciarSessao(login, capitalizarNome(login));
            ConsuHelpApp.navegarPara("/fxml/dashboard.fxml");
        } else {
            exibirErro("Login ou senha incorretos.");
            campoSenha.clear();
        }
    }

    /**
     * Acionado pelo ícone de configurações (menu lateral).
     */
    @FXML
    void onConfiguracoes(ActionEvent event) {
        System.out.println("[ConsuHelp] Configurações abertas.");
        // TODO: abrir painel de configurações (URL do servidor etc.)
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Autenticação local de demonstração.
     * Qualquer login com senha "1234" é aceito para fins de demo.
     */
    private boolean autenticarLocal(String login, String senha) {
        return !login.isEmpty() && senha.equals("1234");
    }

    private void exibirErro(String mensagem) {
        labelErro.setText(mensagem);
        labelErro.setVisible(true);
    }

    private String capitalizarNome(String login) {
        if (login == null || login.isEmpty()) return login;
        return Character.toUpperCase(login.charAt(0)) + login.substring(1);
    }
}
