package com.smartpos.common.service;

import com.smartpos.common.entity.Product;
import java.util.List;

public interface ProductService {
    Product findById(Long id);
    List<Product> findAll();
    List<Product> findByCategory(Long categoryId);
    Product save(Product product);
    void deleteById(Long id);
    List<Product> findByIngredient(Long ingredientId);
    List<Product> findActiveProducts();
}