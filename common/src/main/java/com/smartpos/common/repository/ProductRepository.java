package com.smartpos.common.repository;

import com.smartpos.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategoryId(Long categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    List<Product> findAllActiveProducts();
    
    @Query("SELECT p FROM Product p JOIN p.productIngredients pi WHERE pi.ingredient.id = :ingredientId")
    List<Product> findByIngredientId(Long ingredientId);
}
