// InventoryAnalyticsService.java
package com.smartpos.analytics.service;

import com.smartpos.analytics.dto.IngredientUsageDTO;
import com.smartpos.analytics.dto.InventoryAnalyticsDTO;
import com.smartpos.common.entity.Ingredient;
import com.smartpos.common.entity.InventoryTransaction;
import com.smartpos.common.repository.IngredientRepository;
import com.smartpos.common.repository.InventoryTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryAnalyticsService {

    private final IngredientRepository ingredientRepository;
    private final InventoryTransactionRepository transactionRepository;

    @Autowired
    public InventoryAnalyticsService(IngredientRepository ingredientRepository,
                                    InventoryTransactionRepository transactionRepository) {
        this.ingredientRepository = ingredientRepository;
        this.transactionRepository = transactionRepository;
    }

    public InventoryAnalyticsDTO getInventoryAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<Ingredient> lowStockIngredients = ingredientRepository.findLowStockIngredients();
        
        // Calculate ingredient usage
        List<InventoryTransaction> transactions = transactionRepository
                .findByTransactionDateBetween(startDate, endDate);
        
        // Group transactions by ingredient and calculate usage
        Map<Long, BigDecimal> ingredientUsage = new HashMap<>();
        for (InventoryTransaction transaction : transactions) {
            if ("usage".equals(transaction.getTransactionType())) {
                Long ingredientId = transaction.getIngredient().getId();
                ingredientUsage.merge(ingredientId, transaction.getQuantity(), BigDecimal::add);
            }
        }
        
        // Convert to DTO
        List<IngredientUsageDTO> usageDTOs = ingredients.stream()
                .map(ingredient -> {
                    BigDecimal usage = ingredientUsage.getOrDefault(ingredient.getId(), BigDecimal.ZERO);
                    return IngredientUsageDTO.builder()
                            .ingredientId(ingredient.getId())
                            .ingredientName(ingredient.getName())
                            .usageQuantity(usage)
                            .unitOfMeasure(ingredient.getUnitOfMeasure())
                            .currentStock(ingredient.getCurrentStock())
                            .build();
                })
                .collect(Collectors.toList());
        
        // Sort by usage for most/least used
        List<IngredientUsageDTO> mostUsed = usageDTOs.stream()
                .sorted(Comparator.comparing(IngredientUsageDTO::getUsageQuantity).reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        List<IngredientUsageDTO> leastUsed = usageDTOs.stream()
                .filter(dto -> dto.getUsageQuantity().compareTo(BigDecimal.ZERO) > 0) // Only include used ingredients
                .sorted(Comparator.comparing(IngredientUsageDTO::getUsageQuantity))
                .limit(10)
                .collect(Collectors.toList());
        
        // Calculate average stock level
        Double averageStock = ingredients.stream()
                .map(Ingredient::getCurrentStock)
                .map(BigDecimal::doubleValue)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        
        return InventoryAnalyticsDTO.builder()
                .totalIngredients(ingredients.size())
                .lowStockIngredients(lowStockIngredients.size())
                .mostUsedIngredients(mostUsed)
                .leastUsedIngredients(leastUsed)
                .averageStockLevel(averageStock)
                .build();
    }
}
