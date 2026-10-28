
package com.yottaflex.returns_management.order.dto;

import com.yottaflex.returns_management.order.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateOrderRequest(
        @NotBlank
        @Size(max = 30)
        String orderCode,

        @NotBlank
        String customerCode,

        @NotNull
        OrderStatus status,

        @NotNull
        LocalDateTime orderedAt,

        LocalDateTime deliveredAt,

        @NotNull
        @DecimalMin(value = "0.00", inclusive = true)
        BigDecimal totalPaid
) {}
