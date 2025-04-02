package tesfaye.venieri.software.user;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roleSet = new HashSet<>();

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = hashPassword(password);
        this.email = email;
    }

    public User(String username, String password, String email, Set<String> roles) {
        this.username = username;
        this.password = hashPassword(password);
        this.email = email;
        this.roleSet = roles;
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<String> roleSet) {
        this.roleSet = roleSet;
    }

    public void addRole(String role) {
        this.roleSet.add(role);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
                && Objects.equals(username, user.username)
                && Objects.equals(email, user.email)
                && Objects.equals(roleSet, user.roleSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, roleSet);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roleSet=" + roleSet +
                '}';
    }
}
