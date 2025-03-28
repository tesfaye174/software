package tesfaye.venieri.software;

import java.util.ArrayList;
import java.util.List;

public class StoryDTO {
    private Long id;
    private String title;
    private String content;
    private boolean isEnding;
    private List<ChoiceDTO> choices = new ArrayList<>();
    
    // Costruttori
    public StoryDTO() {}
    
    public StoryDTO(Long id, String title, String content, boolean isEnding) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isEnding = isEnding;
    }
    
    // Costruttore da entità
    public StoryDTO(Story story) {
        this.id = story.getId();
        this.title = story.getTitle();
        this.content = story.getContent();
        this.isEnding = story.isEnding();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public boolean isEnding() {
        return isEnding;
    }
    
    public void setEnding(boolean ending) {
        isEnding = ending;
    }
    
    public List<ChoiceDTO> getChoices() {
        return choices;
    }
    
    public void setChoices(List<ChoiceDTO> choices) {
        this.choices = choices;
    }
    
    // Metodo per convertire DTO in entità
    public Story toEntity() {
        Story story = new Story();
        story.setId(this.id);
        story.setTitle(this.title);
        story.setContent(this.content);
        story.setEnding(this.isEnding);
        return story;
    }
}