package com.smartpos.notification.service;

import com.smartpos.common.entity.Ingredient;
import com.smartpos.common.entity.Order;
import com.smartpos.common.repository.IngredientRepository;
import com.smartpos.common.repository.OrderRepository;
import com.smartpos.notification.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    public void sendNotification(NotificationRequest request) {
        if ("email".equals(request.getType())) {
            emailService.sendEmail(request);
        } else {
            throw new IllegalArgumentException("Unsupported notification type: " + request.getType());
        }
    }
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void checkLowStockIngredients() {
        log.info("Checking for low stock ingredients...");
        List<Ingredient> lowStockIngredients = ingredientRepository.findLowStockIngredients();
        
        if (!lowStockIngredients.isEmpty()) {
            // Send notification to admin
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("ingredients", lowStockIngredients);
            templateData.put("count", lowStockIngredients.size());
            
            NotificationRequest request = NotificationRequest.builder()
                    .type("email")
                    .recipient("admin@smartpos.com") // Replace with actual admin email
                    .subject("Low Stock Alert: " + lowStockIngredients.size() + " ingredients below threshold")
                    .templateName("low-stock-alert")
                    .templateData(templateData)
                    .build();
            
            sendNotification(request);
            log.info("Low stock notification sent for {} ingredients", lowStockIngredients.size());
        }
    }
    
    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    public void sendDailySalesReport() {
        log.info("Generating daily sales report...");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        List<Order> orders = orderRepository.findByOrderDateBetween(yesterday, today);
        
        if (!orders.isEmpty()) {
            BigDecimal totalSales = orders.stream()
                    .map(Order::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int orderCount = orders.size();
            
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("orders", orders);
            templateData.put("orderCount", orderCount);
            templateData.put("totalSales", totalSales);
            templateData.put("date", yesterday.toLocalDate());
            
            NotificationRequest request = NotificationRequest.builder()
                    .type("email")
                    .recipient("manager@smartpos.com") // Replace with actual manager email
                    .subject("Daily Sales Report: " + yesterday.toLocalDate())
                    .templateName("daily-sales-report")
                    .templateData(templateData)
                    .build();
            
            sendNotification(request);
            log.info("Daily sales report sent for {}", yesterday.toLocalDate());
        }
    }
    
    @Scheduled(fixedRate = 60000) // Run every minute
    public void processPendingEmails() {
        emailService.processPendingEmails();
    }
}
