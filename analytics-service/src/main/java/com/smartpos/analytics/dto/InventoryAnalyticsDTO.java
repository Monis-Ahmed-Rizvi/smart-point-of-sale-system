package com.smartpos.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAnalyticsDTO {
    private Integer totalIngredients;
    private Integer lowStockIngredients;
    private List<IngredientUsageDTO> mostUsedIngredients;
    private List<IngredientUsageDTO> leastUsedIngredients;
    private Double averageStockLevel;
}