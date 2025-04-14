package com.smartpos.transaction.service;

import com.smartpos.common.entity.Order;
import com.smartpos.common.entity.OrderItem;
import com.smartpos.common.repository.OrderRepository;
import com.smartpos.common.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Order> findByEmployee(Long employeeId) {
        return orderRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Order order) {
        Order existingOrder = findById(order.getId());
        // Update fields
        existingOrder.setStatus(order.getStatus());
        existingOrder.setPaymentMethod(order.getPaymentMethod());
        existingOrder.setNotes(order.getNotes());
        return orderRepository.save(existingOrder);
    }

    @Override
    @Transactional
    public Order addItemToOrder(Long orderId, OrderItem item) {
        Order order = findById(orderId);
        item.setOrder(order);
        order.addOrderItem(item);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order removeItemFromOrder(Long orderId, Long itemId) {
        Order order = findById(orderId);
        order.getOrderItems().removeIf(item -> item.getId().equals(itemId));
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = findById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Double getTotalSalesForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getTotalSalesForPeriod(startDate, endDate);
    }
}
