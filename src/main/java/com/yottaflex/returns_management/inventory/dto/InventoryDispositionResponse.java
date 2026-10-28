
package com.yottaflex.returns_management.inventory.dto;

import com.yottaflex.returns_management.inventory.DispositionType;
import java.time.LocalDateTime;

public record InventoryDispositionResponse(
        Long id,
        Long inspectionId,
        Long returnLineId,
        String returnCode,
        String sku,
        DispositionType dispositionType,
        String decisionReason,
        LocalDateTime decidedAt
) {
}
