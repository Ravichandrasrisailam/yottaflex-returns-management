
package com.yottaflex.returns_management.customer;

import com.yottaflex.returns_management.customer.dto.CreateCustomerRequest;
import com.yottaflex.returns_management.customer.dto.CustomerResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "A customer with this email already exists"
            );
        }

        if (customerRepository.findByCustomerCode(request.customerCode()).isPresent()) {
            throw new IllegalArgumentException(
                    "This customer code already exists"
            );
        }

        Customer customer = new Customer(
                request.customerCode(),
                request.fullName(),
                request.email()
        );

        Customer saved = customerRepository.save(customer);

        return new CustomerResponse(
                saved.getId(),
                saved.getCustomerCode(),
                saved.getFullName(),
                saved.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public CustomerResponse getByCode(String customerCode) {
        Customer customer = customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new CustomerNotFoundException(customerCode));

        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerCode(),
                customer.getFullName(),
                customer.getEmail()
        );
    }
}
