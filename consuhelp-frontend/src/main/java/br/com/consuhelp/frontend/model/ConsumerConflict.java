package br.com.consuhelp.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumerConflict {
    private UUID id;
    private String description;
    private LocalDateTime createdAt;
    private ActionPlan actionPlan;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ActionPlan getActionPlan() { return actionPlan; }
    public void setActionPlan(ActionPlan actionPlan) { this.actionPlan = actionPlan; }

    // Override toString for nice display in ListView
    @Override
    public String toString() {
        if (actionPlan != null && actionPlan.getTitle() != null) {
            return actionPlan.getTitle();
        }
        if (description != null) {
            return description.substring(0, Math.min(description.length(), 25)) + "...";
        }
        return "Consulta sem título";
    }
}
