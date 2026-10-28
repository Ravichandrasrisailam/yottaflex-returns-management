
package com.yottaflex.returns_management.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank
        @Size(max = 30)
        String customerCode,

        @NotBlank
        @Size(max = 120)
        String fullName,

        @NotBlank
        @Email
        @Size(max = 180)
        String email
) {}
