
package com.yottaflex.returns_management.inventory;

import com.yottaflex.returns_management.returnrequest.Inspection;
import com.yottaflex.returns_management.returnrequest.InspectionCondition;
import com.yottaflex.returns_management.returnrequest.InspectionRepository;
import com.yottaflex.returns_management.returnrequest.ReturnLine;
import com.yottaflex.returns_management.inventory.dto.CreateDispositionRequest;
import com.yottaflex.returns_management.inventory.dto.InventoryDispositionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryDispositionService {

    private final InspectionRepository inspectionRepository;
    private final InventoryDispositionRepository dispositionRepository;

    public InventoryDispositionService(
            InspectionRepository inspectionRepository,
            InventoryDispositionRepository dispositionRepository) {
        this.inspectionRepository = inspectionRepository;
        this.dispositionRepository = dispositionRepository;
    }

    @Transactional
    public InventoryDispositionResponse create(
            CreateDispositionRequest request) {

        Inspection inspection = inspectionRepository
                .findById(request.inspectionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inspection not found: " + request.inspectionId()));

        if (dispositionRepository.existsByInspectionId(inspection.getId())) {
            throw new IllegalArgumentException(
                    "A disposition already exists for this inspection");
        }

        InspectionCondition condition = inspection.getConditionFound();

        if (request.dispositionType() == DispositionType.RESTOCK
                && (condition == InspectionCondition.DAMAGED
                || condition == InspectionCondition.DEFECTIVE
                || condition == InspectionCondition.INCOMPLETE)) {
            throw new IllegalArgumentException(
                    "Damaged, defective, or incomplete items cannot be restocked");
        }

        InventoryDisposition disposition = new InventoryDisposition(
                inspection,
                request.dispositionType(),
                request.decisionReason()
        );

        return toResponse(dispositionRepository.save(disposition));
    }

    @Transactional(readOnly = true)
    public InventoryDispositionResponse getByInspectionId(Long inspectionId) {

        InventoryDisposition disposition = dispositionRepository
                .findByInspection_Id(inspectionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Disposition not found for inspection: " + inspectionId));

        return toResponse(disposition);
    }

    private InventoryDispositionResponse toResponse(
            InventoryDisposition disposition) {

        Inspection inspection = disposition.getInspection();
        ReturnLine line = inspection.getReturnLine();

        return new InventoryDispositionResponse(
                disposition.getId(),
                inspection.getId(),
                line.getId(),
                line.getReturnRequest().getReturnCode(),
                line.getOrderLine().getSku(),
                disposition.getDispositionType(),
                disposition.getDecisionReason(),
                disposition.getDecidedAt()
        );
    }
}
