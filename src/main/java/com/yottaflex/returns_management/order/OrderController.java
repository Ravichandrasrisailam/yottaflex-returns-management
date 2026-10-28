
package com.yottaflex.returns_management.order;

import com.yottaflex.returns_management.order.dto.CreateOrderRequest;
import com.yottaflex.returns_management.order.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(
            @Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{orderCode}")
    public OrderResponse getByCode(
            @PathVariable String orderCode) {
        return orderService.getByCode(orderCode);
    }
}
