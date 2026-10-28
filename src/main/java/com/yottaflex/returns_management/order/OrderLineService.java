
package com.yottaflex.returns_management.order;

import com.yottaflex.returns_management.order.dto.CreateOrderLineRequest;
import com.yottaflex.returns_management.order.dto.OrderLineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderLineService {

    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;

    public OrderLineService(
            OrderRepository orderRepository,
            OrderLineRepository orderLineRepository) {
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
    }

    @Transactional
    public OrderLineResponse addLine(
            String orderCode,
            CreateOrderLineRequest request) {

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderCode));

        List<OrderLine> existingLines =
                orderLineRepository.findByOrderId(order.getId());

        int nextLineNumber = 1;

        for (OrderLine existingLine : existingLines) {
            if (existingLine.getLineNumber() >= nextLineNumber) {
                nextLineNumber = existingLine.getLineNumber() + 1;
            }
        }

        OrderLine line = new OrderLine(
                order,
                nextLineNumber,
                request.sku(),
                request.quantity(),
                request.unitPrice()
        );

        OrderLine saved = orderLineRepository.save(line);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderLineResponse> getLines(String orderCode) {

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderCode));

        List<OrderLine> lines =
                orderLineRepository.findByOrderId(order.getId());

        return lines.stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderLineResponse toResponse(OrderLine line) {
        return new OrderLineResponse(
                line.getId(),
                line.getOrder().getOrderCode(),
                line.getLineNumber(),
                line.getSku(),
                line.getQuantity(),
                line.getUnitPrice()
        );
    }
}
