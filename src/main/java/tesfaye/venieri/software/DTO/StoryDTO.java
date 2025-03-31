package tesfaye.venieri.software.DTO;

import java.util.ArrayList;
import java.util.List;

import tesfaye.venieri.software.Model.Story;

public class StoryDTO {
    private Long id;
    private String title;
    private String content;
    private String description;
    private boolean isPremium;
    private boolean isPublic;
    private int views;
    private List<ChoiceDTO> choices = new ArrayList<>();
    
    // Constructors
    public StoryDTO() {}
    
    public StoryDTO(Long id, String title, String content, String description, 
                   boolean isPremium, boolean isPublic, int views) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.description = description;
        this.isPremium = isPremium;
        this.isPublic = isPublic;
        this.views = views;
    }
    
    // Constructor from entity
    public StoryDTO(Story story) {
        this.id = story.getId();
        this.title = story.getTitle();
        this.content = story.getContent();
        this.description = story.getDescription();
        this.isPremium = story.isPremium();
        this.isPublic = story.getIsPublic();
        this.views = story.getViews();
    }
    
    // Getters and Setters
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isPremium() {
        return isPremium;
    }
    
    public void setPremium(boolean isPremium) {
        this.isPremium = isPremium;
    }
    
    public boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public int getViews() {
        return views;
    }
    
    public void setViews(int views) {
        this.views = views;
    }
    
    public List<ChoiceDTO> getChoices() {
        return choices;
    }
    
    public void setChoices(List<ChoiceDTO> choices) {
        this.choices = choices;
    }
}