package com.smartpos.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientUsageDTO {
    private Long ingredientId;
    private String ingredientName;
    private BigDecimal usageQuantity;
    private String unitOfMeasure;
    private BigDecimal currentStock;
}
