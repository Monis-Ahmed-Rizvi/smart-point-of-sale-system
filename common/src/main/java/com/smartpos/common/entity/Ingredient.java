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
@Table(name = "ingredients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private IngredientCategory category;
    
    @Column(name = "current_stock", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentStock;
    
    @Column(name = "unit_of_measure", nullable = false, length = 20)
    private String unitOfMeasure; // e.g., kg, g, l, ml, each
    
    @Column(name = "minimum_stock", precision = 10, scale = 2, nullable = false)
    private BigDecimal minimumStock = BigDecimal.ZERO;
    
    @Column(name = "cost_per_unit", nullable = false, precision = 10, scale = 4)
    private BigDecimal costPerUnit;
    
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @Column(name = "storage_location", length = 50)
    private String storageLocation;
    
    @Column(name = "shelf_life")
    private Integer shelfLife; // in days
    
    @Column(name = "is_allergen")
    private Boolean isAllergen = false;
    
    @OneToMany(mappedBy = "ingredient")
    private Set<ProductIngredient> productIngredients = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}