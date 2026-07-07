package br.com.consuhelp.backend.domain;

import java.util.List;

public class ActionPlan {
    private String title;
    private String summary;
    private String recommendedPath; // PROCON, Consumidor.gov.br, JEC, etc.
    private List<String> applicableLaws;
    private List<String> steps;
    private List<String> documentsNeeded;
    private String additionalNotes;

    public ActionPlan() {}

    public ActionPlan(String title, String summary, String recommendedPath, List<String> applicableLaws, List<String> steps, List<String> documentsNeeded, String additionalNotes) {
        this.title = title;
        this.summary = summary;
        this.recommendedPath = recommendedPath;
        this.applicableLaws = applicableLaws;
        this.steps = steps;
        this.documentsNeeded = documentsNeeded;
        this.additionalNotes = additionalNotes;
    }

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

    // Simple Builder implementation to match existing service code
    public static ActionPlanBuilder builder() {
        return new ActionPlanBuilder();
    }

    public static class ActionPlanBuilder {
        private String title;
        private String summary;
        private String recommendedPath;
        private List<String> applicableLaws;
        private List<String> steps;
        private List<String> documentsNeeded;
        private String additionalNotes;

        public ActionPlanBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ActionPlanBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public ActionPlanBuilder recommendedPath(String recommendedPath) {
            this.recommendedPath = recommendedPath;
            return this;
        }

        public ActionPlanBuilder applicableLaws(List<String> applicableLaws) {
            this.applicableLaws = applicableLaws;
            return this;
        }

        public ActionPlanBuilder steps(List<String> steps) {
            this.steps = steps;
            return this;
        }

        public ActionPlanBuilder documentsNeeded(List<String> documentsNeeded) {
            this.documentsNeeded = documentsNeeded;
            return this;
        }

        public ActionPlanBuilder additionalNotes(String additionalNotes) {
            this.additionalNotes = additionalNotes;
            return this;
        }

        public ActionPlan build() {
            return new ActionPlan(title, summary, recommendedPath, applicableLaws, steps, documentsNeeded, additionalNotes);
        }
    }
}
