
package com.yottaflex.returns_management.inventory.dto;

import com.yottaflex.returns_management.inventory.DispositionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDispositionRequest(
        @NotNull
        Long inspectionId,

        @NotNull
        DispositionType dispositionType,

        @NotBlank
        @Size(max = 500)
        String decisionReason
) {
}
