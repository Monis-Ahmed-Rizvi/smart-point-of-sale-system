package com.smartpos.inventory.service;

import com.smartpos.common.entity.Ingredient;
import com.smartpos.common.entity.InventoryTransaction;
import com.smartpos.common.entity.Order;
import com.smartpos.common.entity.OrderItem;
import com.smartpos.common.entity.ProductIngredient;
import com.smartpos.common.repository.IngredientRepository;
import com.smartpos.common.repository.InventoryTransactionRepository;
import com.smartpos.common.repository.OrderRepository;
import com.smartpos.common.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final IngredientRepository ingredientRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    
    @Autowired
    public InventoryServiceImpl(IngredientRepository ingredientRepository, 
                               InventoryTransactionRepository transactionRepository,
                               OrderRepository orderRepository) {
        this.ingredientRepository = ingredientRepository;
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
    }
    
    @Override
    public Ingredient findIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found: " + id));
    }
    
    @Override
    public List<Ingredient> findAllIngredients() {
        return ingredientRepository.findAll();
    }
    
    @Override
    public List<Ingredient> findLowStockIngredients() {
        return ingredientRepository.findLowStockIngredients();
    }
    
    @Override
    @Transactional
    public Ingredient updateStock(Long ingredientId, BigDecimal quantity, String transactionType, String notes) {
        Ingredient ingredient = findIngredientById(ingredientId);
        
        // Update stock based on transaction type
        if ("purchase".equals(transactionType) || "adjustment".equals(transactionType)) {
            ingredient.setCurrentStock(ingredient.getCurrentStock().add(quantity));
        } else if ("usage".equals(transactionType) || "waste".equals(transactionType)) {
            if (ingredient.getCurrentStock().compareTo(quantity) < 0) {
                throw new RuntimeException("Insufficient stock for ingredient: " + ingredient.getName());
            }
            ingredient.setCurrentStock(ingredient.getCurrentStock().subtract(quantity));
        } else {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }
        
        // Save updated ingredient
        return ingredientRepository.save(ingredient);
    }
    
    @Override
    @Transactional
    public InventoryTransaction recordTransaction(Long ingredientId, BigDecimal quantity, 
                                                 String transactionType, Long orderId, 
                                                 Long employeeId, String notes) {
        Ingredient ingredient = findIngredientById(ingredientId);
        
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setIngredient(ingredient);
        transaction.setQuantity(quantity);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setNotes(notes);
        
        // Set order if provided
        if (orderId != null) {
            orderRepository.findById(orderId).ifPresent(transaction::setOrder);
        }
        
        // Save transaction
        return transactionRepository.save(transaction);
    }
    
    @Override
    public List<InventoryTransaction> getTransactionHistory(Long ingredientId, 
                                                          LocalDateTime startDate, 
                                                          LocalDateTime endDate) {
        return transactionRepository.findByIngredientIdAndTransactionDateBetween(
            ingredientId, startDate, endDate);
    }
    
    @Override
    @Transactional
    public void processOrderIngredients(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        for (OrderItem item : order.getOrderItems()) {
            for (ProductIngredient productIngredient : item.getProduct().getProductIngredients()) {
                // Calculate quantity needed based on order quantity
                BigDecimal quantityNeeded = productIngredient.getQuantity()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
                
                // Update stock
                updateStock(
                    productIngredient.getIngredient().getId(),
                    quantityNeeded,
                    "usage",
                    "Used in order #" + orderId
                );
                
                // Record transaction
                recordTransaction(
                    productIngredient.getIngredient().getId(),
                    quantityNeeded,
                    "usage",
                    orderId,
                    order.getEmployee().getId(),
                    "Used in order #" + orderId
                );
            }
        }
    }
    
    @Override
    public BigDecimal getCurrentStock(Long ingredientId) {
        return findIngredientById(ingredientId).getCurrentStock();
    }
}
