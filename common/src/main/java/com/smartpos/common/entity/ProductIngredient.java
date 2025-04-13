package com.smartpos.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_ingredients", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "ingredient_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductIngredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;
    
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity; // amount required for one serving
    
    @Column(name = "unit_of_measure", nullable = false, length = 20)
    private String unitOfMeasure;
    
    @Column(name = "is_optional")
    private Boolean isOptional = false;
    
    @Column(name = "is_substitutable")
    private Boolean isSubstitutable = false;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @OneToMany(mappedBy = "productIngredient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IngredientSubstitution> substitutions = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Helper methods to maintain bidirectional relationship
    public void addSubstitution(IngredientSubstitution substitution) {
        substitutions.add(substitution);
        substitution.setProductIngredient(this);
    }
    
    public void removeSubstitution(IngredientSubstitution substitution) {
        substitutions.remove(substitution);
        substitution.setProductIngredient(null);
    }
}
