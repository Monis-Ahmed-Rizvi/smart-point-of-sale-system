package com.smartpos.inventory.controller;

import com.smartpos.common.entity.Ingredient;
import com.smartpos.common.entity.InventoryTransaction;
import com.smartpos.common.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/ingredients")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(inventoryService.findAllIngredients());
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.findIngredientById(id));
    }

    @GetMapping("/ingredients/low-stock")
    public ResponseEntity<List<Ingredient>> getLowStockIngredients() {
        return ResponseEntity.ok(inventoryService.findLowStockIngredients());
    }

    @PostMapping("/update-stock")
    public ResponseEntity<Ingredient> updateStock(
            @RequestParam Long ingredientId,
            @RequestParam BigDecimal quantity,
            @RequestParam String transactionType,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(inventoryService.updateStock(ingredientId, quantity, transactionType, notes));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<InventoryTransaction>> getTransactionHistory(
            @RequestParam Long ingredientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(inventoryService.getTransactionHistory(ingredientId, startDate, endDate));
    }

    @PostMapping("/process-order")
    public ResponseEntity<Void> processOrderIngredients(@RequestParam Long orderId) {
        inventoryService.processOrderIngredients(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/current-stock")
    public ResponseEntity<BigDecimal> getCurrentStock(@RequestParam Long ingredientId) {
        return ResponseEntity.ok(inventoryService.getCurrentStock(ingredientId));
    }
}
