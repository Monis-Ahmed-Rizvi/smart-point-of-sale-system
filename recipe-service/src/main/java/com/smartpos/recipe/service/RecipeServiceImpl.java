package com.smartpos.recipe.service;

import com.smartpos.common.entity.Ingredient;
import com.smartpos.common.entity.Product;
import com.smartpos.common.entity.ProductIngredient;
import com.smartpos.common.repository.IngredientRepository;
import com.smartpos.common.repository.ProductIngredientRepository;
import com.smartpos.common.repository.ProductRepository;
import com.smartpos.common.service.ProductService;
import com.smartpos.common.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final ProductService productService;

    @Autowired
    public RecipeServiceImpl(ProductRepository productRepository, 
                            IngredientRepository ingredientRepository,
                            ProductIngredientRepository productIngredientRepository,
                            ProductService productService) {
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.productService = productService;
    }

    @Override
    @Transactional
    public ProductIngredient addIngredientToProduct(Long productId, ProductIngredient productIngredient) {
        Product product = productService.findById(productId);
        
        Ingredient ingredient = ingredientRepository.findById(productIngredient.getIngredient().getId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        
        productIngredient.setProduct(product);
        productIngredient.setIngredient(ingredient);
        
        product.addIngredient(productIngredient);
        productRepository.save(product);
        
        return productIngredient;
    }

    @Override
    @Transactional
    public void removeIngredientFromProduct(Long productId, Long productIngredientId) {
        Product product = productService.findById(productId);
        
        product.getProductIngredients().removeIf(pi -> pi.getId().equals(productIngredientId));
        productRepository.save(product);
    }

    @Override
    public List<ProductIngredient> getProductIngredients(Long productId) {
        Product product = productService.findById(productId);
        return product.getProductIngredients().stream().collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateProductCost(Long productId) {
        List<ProductIngredient> ingredients = getProductIngredients(productId);
        
        return ingredients.stream()
                .map(pi -> {
                    BigDecimal ingredientCost = pi.getIngredient().getCostPerUnit();
                    return ingredientCost.multiply(pi.getQuantity());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional
    public Product createProductWithIngredients(Product product, List<ProductIngredient> ingredients) {
        Product savedProduct = productService.save(product);
        
        for (ProductIngredient pi : ingredients) {
            addIngredientToProduct(savedProduct.getId(), pi);
        }
        
        return productService.findById(savedProduct.getId());
    }

    @Override
    @Transactional
    public ProductIngredient updateProductIngredient(Long productIngredientId, ProductIngredient updatedIngredient) {
        ProductIngredient existingIngredient = productIngredientRepository.findById(productIngredientId)
                .orElseThrow(() -> new RuntimeException("Product ingredient not found"));
        
        existingIngredient.setQuantity(updatedIngredient.getQuantity());
        existingIngredient.setUnitOfMeasure(updatedIngredient.getUnitOfMeasure());
        existingIngredient.setIsOptional(updatedIngredient.getIsOptional());
        existingIngredient.setIsSubstitutable(updatedIngredient.getIsSubstitutable());
        existingIngredient.setNotes(updatedIngredient.getNotes());
        
        return productIngredientRepository.save(existingIngredient);
    }
}
