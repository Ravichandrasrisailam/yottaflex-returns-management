
package com.yottaflex.returns_management.refund;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    Optional<Refund> findByReturnRequest_Id(Long returnRequestId);

    boolean existsByReturnRequest_Id(Long returnRequestId);

    @Query("""
        SELECT r
        FROM Refund r
        JOIN FETCH r.returnRequest
        WHERE r.id = :refundId
        """)
    Optional<Refund> findByIdWithReturnRequest(
            @Param("refundId") Long refundId);

    @Query("""
        SELECT COALESCE(SUM(r.amount), 0)
        FROM Refund r
        WHERE r.returnRequest.order.id = :orderId
        """)
    BigDecimal sumRefundsForOrder(@Param("orderId") Long orderId);
}
