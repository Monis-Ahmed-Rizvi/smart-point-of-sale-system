// AnalyticsController.java
package com.smartpos.analytics.controller;

import com.smartpos.analytics.dto.InventoryAnalyticsDTO;
import com.smartpos.analytics.dto.SalesAnalyticsDTO;
import com.smartpos.analytics.service.InventoryAnalyticsService;
import com.smartpos.analytics.service.SalesAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;
    private final InventoryAnalyticsService inventoryAnalyticsService;

    @Autowired
    public AnalyticsController(SalesAnalyticsService salesAnalyticsService,
                              InventoryAnalyticsService inventoryAnalyticsService) {
        this.salesAnalyticsService = salesAnalyticsService;
        this.inventoryAnalyticsService = inventoryAnalyticsService;
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesAnalyticsDTO> getSalesAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        SalesAnalyticsDTO analytics = salesAnalyticsService.getSalesAnalytics(start, end);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/inventory")
    public ResponseEntity<InventoryAnalyticsDTO> getInventoryAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        InventoryAnalyticsDTO analytics = inventoryAnalyticsService.getInventoryAnalytics(start, end);
        return ResponseEntity.ok(analytics);
    }
}