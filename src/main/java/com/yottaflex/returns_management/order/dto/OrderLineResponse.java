
package com.yottaflex.returns_management.order.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long id,
        String orderCode,
        Integer lineNumber,
        String sku,
        Integer quantity,
        BigDecimal unitPrice
) {
}
