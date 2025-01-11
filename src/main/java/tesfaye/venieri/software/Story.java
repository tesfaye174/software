package tesfaye.venieri.software;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.List;
import java.util.Objects;

@Entity
public class Story {
    private @Id
    @GeneratedValue Long id;

    private String name;
//    private List<Story> next;

    public Story(){}

    public Story(String name, List<Story> next) {
        this.name = name;
//        this.next = next;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public List<Story> getNext() {
//        return next;
//    }
//
//    public void setNext(List<Story> next) {
//        this.next = next;
//    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Story story)) return false;
        return Objects.equals(id, story.id) && Objects.equals(name, story.name);
//        return Objects.equals(id, story.id) && Objects.equals(name, story.name) && Objects.equals(next, story.next);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
//        return Objects.hash(id, name, next);
    }

    @Override
    public String toString() {
        return "Story{" +
                "id=" + id +
                ", name='" + name + '\'' +
//                ", next=" + next +
                '}';
    }
}
