// SalesAnalyticsService.java
package com.smartpos.analytics.service;

import com.smartpos.analytics.dto.DailySalesDTO;
import com.smartpos.analytics.dto.SalesAnalyticsDTO;
import com.smartpos.analytics.dto.TopProductDTO;
import com.smartpos.common.entity.Order;
import com.smartpos.common.entity.OrderItem;
import com.smartpos.common.entity.Product;
import com.smartpos.common.repository.OrderRepository;
import com.smartpos.common.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalesAnalyticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SalesAnalyticsService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public SalesAnalyticsDTO getSalesAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        
        if (orders.isEmpty()) {
            return new SalesAnalyticsDTO();
        }
        
        // Calculate total sales
        BigDecimal totalSales = orders.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate average order value
        BigDecimal averageOrderValue = totalSales.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);
        
        // Get sales by category
        Map<String, BigDecimal> salesByCategory = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                String categoryName = product.getCategory() != null ? product.getCategory().getName() : "Uncategorized";
                
                BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                salesByCategory.merge(categoryName, itemTotal, BigDecimal::add);
            }
        }
        
        // Get daily sales
        Map<LocalDate, List<Order>> ordersByDate = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getOrderDate().toLocalDate()));
        
        List<DailySalesDTO> dailySales = new ArrayList<>();
        ordersByDate.forEach((date, dateOrders) -> {
            BigDecimal dateSales = dateOrders.stream()
                    .map(Order::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            dailySales.add(DailySalesDTO.builder()
                    .date(date)
                    .totalSales(dateSales)
                    .orderCount(dateOrders.size())
                    .build());
        });
        
        // Sort daily sales by date
        dailySales.sort(Comparator.comparing(DailySalesDTO::getDate));
        
        // Get top products
        Map<Long, TopProductDTO> productMap = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                Long productId = product.getId();
                
                if (!productMap.containsKey(productId)) {
                    productMap.put(productId, TopProductDTO.builder()
                            .productId(productId)
                            .productName(product.getName())
                            .quantity(0)
                            .revenue(BigDecimal.ZERO)
                            .build());
                }
                
                TopProductDTO dto = productMap.get(productId);
                dto.setQuantity(dto.getQuantity() + item.getQuantity());
                dto.setRevenue(dto.getRevenue().add(
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
            }
        }
        
        // Get top 10 products by revenue
        List<TopProductDTO> topProducts = productMap.values().stream()
                .sorted(Comparator.comparing(TopProductDTO::getRevenue).reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        // Get sales by payment method
        Map<String, BigDecimal> salesByPaymentMethod = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getPaymentMethod() != null ? order.getPaymentMethod() : "Unknown",
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotal, BigDecimal::add)));
        
        return SalesAnalyticsDTO.builder()
                .totalSales(totalSales)
                .totalOrders(orders.size())
                .averageOrderValue(averageOrderValue)
                .salesByCategory(salesByCategory)
                .dailySales(dailySales)
                .topProducts(topProducts)
                .salesByPaymentMethod(salesByPaymentMethod)
                .build();
    }
}
