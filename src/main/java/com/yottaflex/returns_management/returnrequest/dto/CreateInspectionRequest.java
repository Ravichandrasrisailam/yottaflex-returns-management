
package com.yottaflex.returns_management.returnrequest.dto;

import com.yottaflex.returns_management.returnrequest.InspectionCondition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateInspectionRequest(
        @NotNull
        Long returnLineId,

        @NotNull
        InspectionCondition conditionFound,

        @Size(max = 1000)
        String notes
) {
}
