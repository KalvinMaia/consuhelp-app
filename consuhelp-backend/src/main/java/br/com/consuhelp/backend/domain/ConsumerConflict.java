package br.com.consuhelp.backend.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConsumerConflict {
    private UUID id;
    private String description;
    private LocalDateTime createdAt;
    private ActionPlan actionPlan;

    public ConsumerConflict() {}

    public ConsumerConflict(UUID id, String description, LocalDateTime createdAt, ActionPlan actionPlan) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
        this.actionPlan = actionPlan;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ActionPlan getActionPlan() { return actionPlan; }
    public void setActionPlan(ActionPlan actionPlan) { this.actionPlan = actionPlan; }

    // Simple Builder implementation to match existing service code
    public static ConsumerConflictBuilder builder() {
        return new ConsumerConflictBuilder();
    }

    public static class ConsumerConflictBuilder {
        private UUID id;
        private String description;
        private LocalDateTime createdAt;
        private ActionPlan actionPlan;

        public ConsumerConflictBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ConsumerConflictBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ConsumerConflictBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ConsumerConflictBuilder actionPlan(ActionPlan actionPlan) {
            this.actionPlan = actionPlan;
            return this;
        }

        public ConsumerConflict build() {
            return new ConsumerConflict(id, description, createdAt, actionPlan);
        }
    }
}
