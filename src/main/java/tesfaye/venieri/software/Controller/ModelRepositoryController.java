package tesfaye.venieri.software.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a generic controller template that can be extended by specific entity controllers
 * to provide standardized CRUD operations.
 * 
 * @param <T> The entity type
 * @param <R> The repository type
 */
@RestController
public abstract class ModelRepositoryController<T, R> {

    @Autowired
    protected R repository;

    /**
     * Get all entities
     * 
     * @return List of all entities
     */
    @GetMapping
    public abstract List<T> getAll();

    /**
     * Get entity by ID
     * 
     * @param id The entity ID
     * @return The entity
     */
    @GetMapping("/{id}")
    public abstract ResponseEntity<T> getById(@PathVariable Long id);

    /**
     * Create a new entity
     * 
     * @param entity The entity to create
     * @return The created entity
     */
    @PostMapping
    public abstract T create(@RequestBody T entity);

    /**
     * Update an existing entity
     * 
     * @param id The entity ID
     * @param entityDetails The updated entity details
     * @return The updated entity
     */
    @PutMapping("/{id}")
    public abstract ResponseEntity<T> update(@PathVariable Long id, @RequestBody T entityDetails);

    /**
     * Delete an entity
     * 
     * @param id The entity ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable Long id) throws ResourceNotFoundException {
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}