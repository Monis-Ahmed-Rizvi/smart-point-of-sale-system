package com.smartpos.common.service;

import com.smartpos.common.entity.Ingredient;
import com.smartpos.common.entity.InventoryTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InventoryService {
    Ingredient findIngredientById(Long id);
    List<Ingredient> findAllIngredients();
    List<Ingredient> findLowStockIngredients();
    Ingredient updateStock(Long ingredientId, BigDecimal quantity, String transactionType, String notes);
    InventoryTransaction recordTransaction(Long ingredientId, BigDecimal quantity, String transactionType, Long orderId, Long employeeId, String notes);
    List<InventoryTransaction> getTransactionHistory(Long ingredientId, LocalDateTime startDate, LocalDateTime endDate);
    void processOrderIngredients(Long orderId);
    BigDecimal getCurrentStock(Long ingredientId);
}