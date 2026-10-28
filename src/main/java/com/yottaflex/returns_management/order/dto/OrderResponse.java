
package com.yottaflex.returns_management.order.dto;

import com.yottaflex.returns_management.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String orderCode,
        String customerCode,
        OrderStatus status,
        LocalDateTime orderedAt,
        LocalDateTime deliveredAt,
        BigDecimal totalPaid
) {}
