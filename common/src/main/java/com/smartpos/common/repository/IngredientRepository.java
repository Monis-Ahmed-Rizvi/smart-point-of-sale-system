package com.smartpos.common.repository;

import com.smartpos.common.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    List<Ingredient> findByNameContainingIgnoreCase(String name);
    
    List<Ingredient> findByCategoryId(Long categoryId);
    
    @Query("SELECT i FROM Ingredient i WHERE i.currentStock <= i.minimumStock")
    List<Ingredient> findLowStockIngredients();
    
    @Query("SELECT i FROM Ingredient i WHERE i.isAllergen = true")
    List<Ingredient> findAllAllergens();
}