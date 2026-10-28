
package com.yottaflex.returns_management.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryDispositionRepository
        extends JpaRepository<InventoryDisposition, Long> {

    Optional<InventoryDisposition> findByInspection_Id(Long inspectionId);

    boolean existsByInspectionId(Long inspectionId);
}
