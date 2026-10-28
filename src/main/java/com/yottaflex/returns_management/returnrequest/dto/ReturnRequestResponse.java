
package com.yottaflex.returns_management.returnrequest.dto;

import com.yottaflex.returns_management.returnrequest.ReturnRequestStatus;

import java.time.LocalDateTime;

public record ReturnRequestResponse(
        Long id,
        String returnCode,
        String orderCode,
        ReturnRequestStatus status,
        String reason,
        LocalDateTime requestedAt
) {
}
