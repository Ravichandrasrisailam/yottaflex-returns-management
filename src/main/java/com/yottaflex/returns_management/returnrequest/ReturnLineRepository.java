
package com.yottaflex.returns_management.returnrequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnLineRepository
        extends JpaRepository<ReturnLine, Long> {

    List<ReturnLine> findByReturnRequest_Id(Long returnRequestId);

    boolean existsByReturnRequestIdAndOrderLineId(
            Long returnRequestId,
            Long orderLineId);
}
