package com.smartpos.common.service;

import com.smartpos.common.entity.Product;
import com.smartpos.common.entity.ProductIngredient;

import java.math.BigDecimal;
import java.util.List;

public interface RecipeService {
    ProductIngredient addIngredientToProduct(Long productId, ProductIngredient productIngredient);
    void removeIngredientFromProduct(Long productId, Long productIngredientId);
    List<ProductIngredient> getProductIngredients(Long productId);
    BigDecimal calculateProductCost(Long productId);
    Product createProductWithIngredients(Product product, List<ProductIngredient> ingredients);
    ProductIngredient updateProductIngredient(Long productIngredientId, ProductIngredient updatedIngredient);
}