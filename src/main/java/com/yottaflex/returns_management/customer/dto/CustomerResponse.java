
package com.yottaflex.returns_management.customer.dto;

public record CustomerResponse(
        Long id,
        String customerCode,
        String fullName,
        String email
) {}
