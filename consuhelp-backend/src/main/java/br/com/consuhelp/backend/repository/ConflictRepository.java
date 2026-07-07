package br.com.consuhelp.backend.repository;

import br.com.consuhelp.backend.domain.ConsumerConflict;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ConflictRepository {
    private final Map<UUID, ConsumerConflict> storage = new ConcurrentHashMap<>();

    public ConsumerConflict save(ConsumerConflict conflict) {
        if (conflict.getId() == null) {
            conflict.setId(UUID.randomUUID());
        }
        storage.put(conflict.getId(), conflict);
        return conflict;
    }

    public List<ConsumerConflict> findAll() {
        List<ConsumerConflict> list = new ArrayList<>(storage.values());
        // Sort by createdAt descending
        list.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));
        return list;
    }

    public Optional<ConsumerConflict> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public boolean delete(UUID id) {
        return storage.remove(id) != null;
    }

    public void clear() {
        storage.clear();
    }
}
