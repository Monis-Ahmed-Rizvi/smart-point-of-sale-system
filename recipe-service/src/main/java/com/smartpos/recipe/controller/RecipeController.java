package com.smartpos.recipe.controller;

import com.smartpos.common.entity.Product;
import com.smartpos.common.entity.ProductIngredient;
import com.smartpos.common.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/products/{productId}/ingredients")
    public ResponseEntity<List<ProductIngredient>> getProductIngredients(@PathVariable Long productId) {
        return ResponseEntity.ok(recipeService.getProductIngredients(productId));
    }

    @PostMapping("/products/{productId}/ingredients")
    public ResponseEntity<ProductIngredient> addIngredientToProduct(
            @PathVariable Long productId, @RequestBody ProductIngredient productIngredient) {
        return ResponseEntity.ok(recipeService.addIngredientToProduct(productId, productIngredient));
    }

    @PutMapping("/products/ingredients/{productIngredientId}")
    public ResponseEntity<ProductIngredient> updateProductIngredient(
            @PathVariable Long productIngredientId, @RequestBody ProductIngredient productIngredient) {
        return ResponseEntity.ok(recipeService.updateProductIngredient(productIngredientId, productIngredient));
    }

    @DeleteMapping("/products/{productId}/ingredients/{productIngredientId}")
    public ResponseEntity<Void> removeIngredientFromProduct(
            @PathVariable Long productId, @PathVariable Long productIngredientId) {
        recipeService.removeIngredientFromProduct(productId, productIngredientId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products/{productId}/cost")
    public ResponseEntity<Map<String, BigDecimal>> calculateProductCost(@PathVariable Long productId) {
        BigDecimal cost = recipeService.calculateProductCost(productId);
        return ResponseEntity.ok(Map.of("cost", cost));
    }

    @PostMapping("/products/with-ingredients")
    public ResponseEntity<Product> createProductWithIngredients(
            @RequestBody Map<String, Object> request) {
        Product product = new Product(); // Parse from request
        List<ProductIngredient> ingredients = List.of(); // Parse from request
        
        return ResponseEntity.ok(recipeService.createProductWithIngredients(product, ingredients));
    }
}
