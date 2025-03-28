package tesfaye.venieri.software;

public class ChoiceDTO {
    private Long id;
    private String text;
    private Long currentStoryId;
    private Long nextStoryId;
    
    // Costruttori
    public ChoiceDTO() {}
    
    public ChoiceDTO(Long id, String text, Long currentStoryId, Long nextStoryId) {
        this.id = id;
        this.text = text;
        this.currentStoryId = currentStoryId;
        this.nextStoryId = nextStoryId;
    }
    
    // Costruttore da entità
    public ChoiceDTO(Choice choice) {
        this.id = choice.getId();
        this.text = choice.getText();
        if (choice.getCurrentStory() != null) {
            this.currentStoryId = choice.getCurrentStory().getId();
        }
        if (choice.getNextStory() != null) {
            this.nextStoryId = choice.getNextStory().getId();
        }
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Long getCurrentStoryId() {
        return currentStoryId;
    }
    
    public void setCurrentStoryId(Long currentStoryId) {
        this.currentStoryId = currentStoryId;
    }
    
    public Long getNextStoryId() {
        return nextStoryId;
    }
    
    public void setNextStoryId(Long nextStoryId) {
        this.nextStoryId = nextStoryId;
    }
}