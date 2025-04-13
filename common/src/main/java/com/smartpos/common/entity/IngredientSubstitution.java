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

@Entity
@Table(name = "ingredient_substitutions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientSubstitution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_ingredient_id", nullable = false)
    private ProductIngredient productIngredient;
    
    @ManyToOne
    @JoinColumn(name = "substitute_ingredient_id", nullable = false)
    private Ingredient substituteIngredient;
    
    @Column(name = "quantity_adjustment_factor", precision = 5, scale = 2, nullable = false)
    private BigDecimal quantityAdjustmentFactor = BigDecimal.ONE; // multiplication factor for quantity
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}