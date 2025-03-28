package tesfaye.venieri.software;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Choice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String text;
    
    @ManyToOne
    @JoinColumn(name = "current_story_id")
    private Story currentStory;
    
    @ManyToOne
    @JoinColumn(name = "next_story_id")
    private Story nextStory;
    
    // Costruttori
    public Choice() {}
    
    public Choice(String text, Story nextStory) {
        this.text = text;
        this.nextStory = nextStory;
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
    
    public Story getCurrentStory() {
        return currentStory;
    }
    
    public void setCurrentStory(Story currentStory) {
        this.currentStory = currentStory;
    }
    
    public Story getNextStory() {
        return nextStory;
    }
    
    public void setNextStory(Story nextStory) {
        this.nextStory = nextStory;
    }
}