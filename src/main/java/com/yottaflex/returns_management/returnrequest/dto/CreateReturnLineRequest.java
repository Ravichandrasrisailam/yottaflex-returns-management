
package com.yottaflex.returns_management.returnrequest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReturnLineRequest(
        @NotNull
        @Min(1)
        Long orderLineId,

        @NotNull
        @Min(1)
        Integer quantityRequested
) {
}
