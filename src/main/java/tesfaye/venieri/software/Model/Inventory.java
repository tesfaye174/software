package tesfaye.venieri.software.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "inventories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<CollectedItem> collectedItems = new HashSet<>();

    // Miglioramento della documentazione
    // Classe che rappresenta un inventario
    
    // Metodo per verificare se l'inventario contiene un determinato oggetto
    // Metodo per aggiungere un oggetto all'inventario
    // Metodo per rimuovere un oggetto dall'inventario
    // Metodo per restituire tutti gli oggetti nell'inventario
    public Set<Item> getItems() {
        Set<Item> items = new HashSet<>();
        for (CollectedItem collectedItem : collectedItems) {
            items.add(collectedItem.getItem());
        }
        return items;
    }
}