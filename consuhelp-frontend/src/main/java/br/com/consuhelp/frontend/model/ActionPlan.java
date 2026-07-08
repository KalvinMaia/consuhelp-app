package br.com.consuhelp.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionPlan {
    private String title;
    private String summary;
    private String recommendedPath;
    private List<String> applicableLaws;
    private List<String> steps;
    private List<String> documentsNeeded;
    private String additionalNotes;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getRecommendedPath() { return recommendedPath; }
    public void setRecommendedPath(String recommendedPath) { this.recommendedPath = recommendedPath; }

    public List<String> getApplicableLaws() { return applicableLaws; }
    public void setApplicableLaws(List<String> applicableLaws) { this.applicableLaws = applicableLaws; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public List<String> getDocumentsNeeded() { return documentsNeeded; }
    public void setDocumentsNeeded(List<String> documentsNeeded) { this.documentsNeeded = documentsNeeded; }

    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
}
