
package com.yottaflex.returns_management.returnrequest;
import com.yottaflex.returns_management.returnrequest.ReturnLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InspectionRepository
        extends JpaRepository<Inspection, Long> {

    Optional<Inspection> findByReturnLine_Id(Long returnLineId);

    boolean existsByReturnLineId(Long returnLineId);
}
