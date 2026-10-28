
package com.yottaflex.returns_management.returnrequest.shipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnShipmentRepository
        extends JpaRepository<ReturnShipment, Long> {

    Optional<ReturnShipment> findByShipmentCode(String shipmentCode);

    List<ReturnShipment> findByReturnRequestId(Long returnRequestId);

    boolean existsByShipmentCode(String shipmentCode);
}
