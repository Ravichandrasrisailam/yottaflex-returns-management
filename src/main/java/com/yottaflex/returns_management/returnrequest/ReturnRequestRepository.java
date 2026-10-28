
package com.yottaflex.returns_management.returnrequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnRequestRepository
        extends JpaRepository<ReturnRequest, Long> {

    Optional<ReturnRequest> findByReturnCode(String returnCode);

    boolean existsByReturnCode(String returnCode);
}
