package com.smartpos.common.service;

import com.smartpos.common.entity.Order;
import com.smartpos.common.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Order findById(Long id);
    List<Order> findAll();
    List<Order> findByCustomer(Long customerId);
    List<Order> findByEmployee(Long employeeId);
    List<Order> findByStatus(String status);
    List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Order createOrder(Order order);
    Order updateOrder(Order order);
    Order addItemToOrder(Long orderId, OrderItem item);
    Order removeItemFromOrder(Long orderId, Long itemId);
    Order updateOrderStatus(Long orderId, String status);
    void deleteOrder(Long id);
    Double getTotalSalesForPeriod(LocalDateTime startDate, LocalDateTime endDate);
}