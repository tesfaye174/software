package tesfaye.venieri.software.DTO;

import tesfaye.venieri.software.Model.Choice;

public class ChoiceDTO {
    private Long id;
    private String text;
    private Long currentSceneId;
    private Long nextSceneId;
    
    // Constructors
    public ChoiceDTO() {}
    
    public ChoiceDTO(Long id, String text, Long currentSceneId, Long nextSceneId) {
        this.id = id;
        this.text = text;
        this.currentSceneId = currentSceneId;
        this.nextSceneId = nextSceneId;
    }
    
    // Constructor from entity
    public ChoiceDTO(Choice choice) {
        this.id = choice.getId();
        this.text = choice.getText();
        if (choice.getScene() != null) {
            this.currentSceneId = choice.getScene().getId();
        }
        if (choice.getNextScene() != null) {
            this.nextSceneId = choice.getNextScene().getId();
        }
    }
    
    // Getters and Setters
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
    
    public Long getCurrentSceneId() {
        return currentSceneId;
    }
    
    public void setCurrentSceneId(Long currentSceneId) {
        this.currentSceneId = currentSceneId;
    }
    
    public Long getNextSceneId() {
        return nextSceneId;
    }
    
    public void setNextSceneId(Long nextSceneId) {
        this.nextSceneId = nextSceneId;
    }
}