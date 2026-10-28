
package com.yottaflex.returns_management.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateOrderLineRequest(
        @NotBlank
        @Size(max = 50)
        String sku,

        @NotNull
        @Min(1)
        Integer quantity,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal unitPrice
) {
}
