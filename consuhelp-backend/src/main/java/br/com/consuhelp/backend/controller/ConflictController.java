package br.com.consuhelp.backend.controller;

import br.com.consuhelp.backend.domain.ConsumerConflict;
import br.com.consuhelp.backend.service.ConflictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conflicts")
@CrossOrigin(origins = "*") // Allow requests from any origin (e.g. if run inside a web browser/app in the future)
public class ConflictController {

    @Autowired
    private ConflictService service;

    public record ConflictRequest(String description) {}

    @PostMapping
    public ResponseEntity<?> createConflict(@RequestBody ConflictRequest request) {
        try {
            ConsumerConflict conflict = service.createAndAnalyze(request.description());
            return ResponseEntity.status(HttpStatus.CREATED).body(conflict);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar a análise: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ConsumerConflict>> getAllConflicts() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsumerConflict> getConflictById(@PathVariable UUID id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConflict(@PathVariable UUID id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
