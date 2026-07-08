package br.com.consuhelp.backend.service;

import br.com.consuhelp.backend.domain.ActionPlan;
import br.com.consuhelp.backend.domain.ConsumerConflict;
import br.com.consuhelp.backend.repository.ConflictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConflictService {

    @Autowired
    private ConflictRepository repository;

    @Autowired
    private GeminiService geminiService;

    public ConsumerConflict createAndAnalyze(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do conflito não pode estar vazia.");
        }

        // Generate the action plan using Gemini (or the fallback mock)
        ActionPlan plan = geminiService.analyzeConflict(description);

        ConsumerConflict conflict = ConsumerConflict.builder()
                .description(description)
                .createdAt(LocalDateTime.now())
                .actionPlan(plan)
                .build();

        return repository.save(conflict);
    }

    public List<ConsumerConflict> getAll() {
        return repository.findAll();
    }

    public Optional<ConsumerConflict> getById(UUID id) {
        return repository.findById(id);
    }

    public boolean delete(UUID id) {
        return repository.delete(id);
    }
}
