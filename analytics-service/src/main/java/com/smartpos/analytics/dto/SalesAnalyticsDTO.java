// SalesAnalyticsDTO.java
package com.smartpos.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsDTO {
    private BigDecimal totalSales;
    private Integer totalOrders;
    private BigDecimal averageOrderValue;
    private Map<String, BigDecimal> salesByCategory;
    private List<DailySalesDTO> dailySales;
    private List<TopProductDTO> topProducts;
    private Map<String, BigDecimal> salesByPaymentMethod;
}