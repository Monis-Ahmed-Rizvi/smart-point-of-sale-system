package com.smartpos.common.repository;

import com.smartpos.common.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    
    List<InventoryTransaction> findByIngredientId(Long ingredientId);
    
    List<InventoryTransaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<InventoryTransaction> findByIngredientIdAndTransactionDateBetween(
        Long ingredientId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<InventoryTransaction> findByOrderId(Long orderId);
}
