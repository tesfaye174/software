package tesfaye.venieri.software.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome utente è obbligatorio")
    @Size(min = 3, max = 50, message = "Il nome utente deve essere compreso tra 3 e 50 caratteri")
    @Column(unique = true)
    private String username;
    
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email non è valida")
    @Column(unique = true)
    private String email;
    
    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 6, message = "La password deve essere di almeno 6 caratteri")
    private String password;
    
    @Column(name = "is_premium", nullable = false)
    private boolean isPremium = false;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Story> stories = new HashSet<>();
    
    @Column(name = "premium_expiration")
    private LocalDateTime premiumExpiration;

    /**
     * Verifica se l'abbonamento premium è ancora valido
     * @return true se l'abbonamento è valido, false altrimenti
     */
    public boolean isPremiumValid() {
        return isPremium && (premiumExpiration == null || premiumExpiration.isAfter(LocalDateTime.now()));
    }

    /**
     * Aggiunge una storia all'utente
     * @param story la storia da aggiungere
     */
    public void addStory(Story story) {
        stories.add(story);
        story.setAuthor(this);
    }

    /**
     * Rimuove una storia dall'utente
     * @param story la storia da rimuovere
     */
    public void removeStory(Story story) {
        stories.remove(story);
        story.setAuthor(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && 
               Objects.equals(username, user.username) && 
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
}