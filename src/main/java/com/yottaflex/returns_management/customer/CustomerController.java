
package com.yottaflex.returns_management.customer;

import com.yottaflex.returns_management.customer.dto.CreateCustomerRequest;
import com.yottaflex.returns_management.customer.dto.CustomerResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(
            @Valid @RequestBody CreateCustomerRequest request) {
        return customerService.create(request);
    }

    @GetMapping("/{customerCode}")
    public CustomerResponse getByCode(@PathVariable String customerCode) {
        return customerService.getByCode(customerCode);
    }
}
