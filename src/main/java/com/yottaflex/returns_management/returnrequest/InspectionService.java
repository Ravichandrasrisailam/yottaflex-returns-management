
package com.yottaflex.returns_management.returnrequest;

import com.yottaflex.returns_management.returnrequest.dto.CreateInspectionRequest;
import com.yottaflex.returns_management.returnrequest.dto.InspectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InspectionService {

    private final ReturnLineRepository returnLineRepository;
    private final InspectionRepository inspectionRepository;

    public InspectionService(
            ReturnLineRepository returnLineRepository,
            InspectionRepository inspectionRepository) {
        this.returnLineRepository = returnLineRepository;
        this.inspectionRepository = inspectionRepository;
    }

    @Transactional
    public InspectionResponse inspect(CreateInspectionRequest request) {

        ReturnLine returnLine = returnLineRepository
                .findById(request.returnLineId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Return line not found: " + request.returnLineId()));

        if (returnLine.getReturnRequest().getStatus()
                != ReturnRequestStatus.APPROVED) {
            throw new IllegalArgumentException(
                    "Only approved return requests can be inspected");
        }

        if (inspectionRepository.existsByReturnLineId(returnLine.getId())) {
            throw new IllegalArgumentException(
                    "This return line has already been inspected");
        }

        Inspection inspection = new Inspection(
                returnLine,
                request.conditionFound(),
                request.notes()
        );

        Inspection saved = inspectionRepository.save(inspection);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public InspectionResponse getByReturnLineId(Long returnLineId) {

        Inspection inspection = inspectionRepository
                .findByReturnLine_Id(returnLineId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inspection not found for return line: " + returnLineId));

        return toResponse(inspection);
    }

    private InspectionResponse toResponse(Inspection inspection) {
        ReturnLine line = inspection.getReturnLine();

        return new InspectionResponse(
                inspection.getId(),
                line.getId(),
                line.getReturnRequest().getReturnCode(),
                line.getOrderLine().getSku(),
                inspection.getConditionFound(),
                inspection.getNotes(),
                inspection.getInspectedAt()
        );
    }
}
