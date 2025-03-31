package tesfaye.venieri.software.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "collected_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt = LocalDateTime.now();

    @Column(name = "quantity")
    private int quantity = 1;

    /**
     * Incrementa la quantità dell'oggetto nell'inventario
     */
    public void incrementQuantity() {
        this.quantity++;
    }

    /**
     * Decrementa la quantità dell'oggetto nell'inventario
     */
    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }

    /**
     * Verifica se ci sono ancora oggetti disponibili
     * @return true se la quantità è maggiore di 0
     */
    public boolean hasQuantityRemaining() {
        return this.quantity > 0;
    }
}