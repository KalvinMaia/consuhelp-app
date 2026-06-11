package com.consuhelp.repository;

import com.consuhelp.model.Consulta;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Repositório em memória para armazenamento das consultas realizadas.
 * <p>
 * Utiliza uma coleção {@code HashMap} indexada por um
 * ID único auto-gerado (UUID), garantindo simplicidade e alta performance
 * operacional sem dependências de bancos SQL nesta etapa.
 * </p>
 */
@Repository
public class ConsultaRepository {

    /** Estrutura de dados principal: HashMap<UUID, Consulta> */
    private final Map<UUID, Consulta> store = new HashMap<>();

    /**
     * Persiste uma nova consulta no repositório.
     *
     * @param consulta entidade a ser salva
     * @return a própria entidade com ID preenchido
     */
    public Consulta salvar(Consulta consulta) {
        store.put(consulta.getId(), consulta);
        return consulta;
    }

    /**
     * Busca uma consulta pelo seu UUID único.
     *
     * @param id identificador da consulta
     * @return Opcional contendo a consulta, ou vazio se não encontrada
     */
    public Optional<Consulta> buscarPorId(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Retorna todas as consultas armazenadas em memória.
     *
     * @return lista imutável de todas as consultas
     */
    public List<Consulta> listarTodas() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }

    /**
     * Remove uma consulta pelo ID.
     *
     * @param id identificador da consulta a remover
     * @return true se a consulta existia e foi removida, false caso contrário
     */
    public boolean remover(UUID id) {
        return store.remove(id) != null;
    }

    /**
     * Retorna o total de consultas armazenadas.
     */
    public int contar() {
        return store.size();
    }
}
