
package com.yottaflex.returns_management.refund;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class RefundRequest {

    @NotNull(message = "Return request ID is required")
    @Positive(message = "Return request ID must be positive")
    private Long returnRequestId;

    public Long getReturnRequestId() {
        return returnRequestId;
    }

    public void setReturnRequestId(Long returnRequestId) {
        this.returnRequestId = returnRequestId;
    }
}
