
package com.yottaflex.returns_management.order;

import com.yottaflex.returns_management.customer.Customer;
import com.yottaflex.returns_management.customer.CustomerNotFoundException;
import com.yottaflex.returns_management.customer.CustomerRepository;
import com.yottaflex.returns_management.order.dto.CreateOrderRequest;
import com.yottaflex.returns_management.order.dto.OrderResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {

        if (orderRepository.findByOrderCode(request.orderCode()).isPresent()) {
            throw new IllegalArgumentException("Order code already exists");
        }

        Customer customer = customerRepository
                .findByCustomerCode(request.customerCode())
                .orElseThrow(() ->
                        new CustomerNotFoundException(request.customerCode()));

        if (request.deliveredAt() != null
                && request.deliveredAt().isBefore(request.orderedAt())) {
            throw new IllegalArgumentException(
                    "Delivery time cannot be before order time");
        }

        if (request.status() == OrderStatus.DELIVERED
                && request.deliveredAt() == null) {
            throw new IllegalArgumentException(
                    "Delivered orders must have a delivery timestamp");
        }

        if (request.totalPaid() == null
                || request.totalPaid().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Total paid cannot be negative or missing");
        }

        Order order = new Order(
                request.orderCode(),
                customer,
                request.status(),
                request.orderedAt(),
                request.deliveredAt(),
                request.totalPaid()
        );

        Order saved = orderRepository.save(order);

       return new OrderResponse(
        saved.getId(),
        saved.getOrderCode(),
        saved.getCustomer().getCustomerCode(),
        saved.getStatus(),
        saved.getOrderedAt(),
        saved.getDeliveredAt(),
        saved.getTotalPaid()
);
    }
    
@Transactional(readOnly = true)
public OrderResponse getByCode(String orderCode) {

    Order order = orderRepository.findByOrderCode(orderCode)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Order not found: " + orderCode));

    return new OrderResponse(
            order.getId(),
            order.getOrderCode(),
            order.getCustomer().getCustomerCode(),
            order.getStatus(),
            order.getOrderedAt(),
            order.getDeliveredAt(),
            order.getTotalPaid()
    );
}

}
