
package com.yottaflex.returns_management.order;

import com.yottaflex.returns_management.order.dto.CreateOrderLineRequest;
import com.yottaflex.returns_management.order.dto.OrderLineResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderCode}/lines")
public class OrderLineController {

    private final OrderLineService orderLineService;

    public OrderLineController(OrderLineService orderLineService) {
        this.orderLineService = orderLineService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderLineResponse addLine(
            @PathVariable String orderCode,
            @Valid @RequestBody CreateOrderLineRequest request) {
        return orderLineService.addLine(orderCode, request);
    }

    @GetMapping
    public List<OrderLineResponse> getLines(
            @PathVariable String orderCode) {
        return orderLineService.getLines(orderCode);
    }
}
