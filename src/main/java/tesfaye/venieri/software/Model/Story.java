package tesfaye.venieri.software.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "stories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    @Size(min = 10, max = 1000)
    @Column(length = 1000)
    private String content;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    private boolean isEnding = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Scene> scenes = new HashSet<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Choice> choices = new HashSet<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Game> games = new HashSet<>();

    public Story(String title, String content, boolean isEnding) {
        this.title = title;
        this.content = content;
        this.isEnding = isEnding;
    }
    
    public Story(String title, String content, boolean isEnding, User author) {
        this.title = title;
        this.content = content;
        this.isEnding = isEnding;
        this.author = author;
    }

    public void addScene(Scene scene) {
        scenes.add(scene);
        scene.setStory(this);
    }
    
    public void removeScene(Scene scene) {
        scenes.remove(scene);
        scene.setStory(null);
    }

    public void addChoice(Choice choice) {
        choices.add(choice);
        choice.setScene(null);
    }
    
    public void removeChoice(Choice choice) {
        choices.remove(choice);
        choice.setScene(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Story story = (Story) o;
        return Objects.equals(id, story.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Story{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isEnding=" + isEnding +
                "}";
    }
}