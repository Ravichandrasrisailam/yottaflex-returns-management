
package com.yottaflex.returns_management.returnrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReturnRequest(
        @NotBlank
        @Size(max = 30)
        String returnCode,

        @NotBlank
        @Size(max = 30)
        String orderCode,

        @NotBlank
        @Size(max = 500)
        String reason
) {
}
