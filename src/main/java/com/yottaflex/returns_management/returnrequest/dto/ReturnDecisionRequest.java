
package com.yottaflex.returns_management.returnrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReturnDecisionRequest(
        @NotBlank
        @Size(max = 500)
        String reason
) {
}
