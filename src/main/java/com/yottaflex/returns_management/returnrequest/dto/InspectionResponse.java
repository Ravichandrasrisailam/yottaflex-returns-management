
package com.yottaflex.returns_management.returnrequest.dto;

import com.yottaflex.returns_management.returnrequest.InspectionCondition;
import java.time.LocalDateTime;

public record InspectionResponse(
        Long id,
        Long returnLineId,
        String returnCode,
        String sku,
        InspectionCondition conditionFound,
        String notes,
        LocalDateTime inspectedAt
) {
}
