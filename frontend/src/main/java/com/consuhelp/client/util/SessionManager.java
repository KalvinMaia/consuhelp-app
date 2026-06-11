package com.consuhelp.client.util;

/**
 * Gerenciador de sessão do usuário logado.
 * <p>
 * Singleton simples que mantém em memória o estado da sessão atual,
 * incluindo o nome do consumidor autenticado para uso nas requisições à API.
 * </p>
 */
public class SessionManager {

    private static SessionManager instancia;

    private String nomeUsuario;
    private String loginUsuario;
    private boolean autenticado = false;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instancia == null) {
            instancia = new SessionManager();
        }
        return instancia;
    }

    /**
     * Registra a sessão após autenticação bem-sucedida.
     *
     * @param login login do usuário
     * @param nome  nome de exibição
     */
    public void iniciarSessao(String login, String nome) {
        this.loginUsuario = login;
        this.nomeUsuario  = nome;
        this.autenticado  = true;
    }

    /** Encerra a sessão atual (logout). */
    public void encerrarSessao() {
        this.loginUsuario = null;
        this.nomeUsuario  = null;
        this.autenticado  = false;
    }

    public boolean isAutenticado() { return autenticado; }
    public String getNomeUsuario()  { return nomeUsuario; }
    public String getLoginUsuario() { return loginUsuario; }
}
