
package com.yottaflex.returns_management.returnrequest.dto;

public record ReturnLineResponse(
        Long id,
        String returnCode,
        String orderCode,
        Integer orderLineNumber,
        String sku,
        Integer quantityRequested
) {
}
