
package com.yottaflex.returns_management.customer;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String customerCode) {
        super("Customer not found: " + customerCode);
    }
}
