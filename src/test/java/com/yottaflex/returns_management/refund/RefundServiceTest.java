
package com.yottaflex.returns_management.refund;

import com.yottaflex.returns_management.inventory.InventoryDisposition;
import com.yottaflex.returns_management.inventory.InventoryDispositionRepository;
import com.yottaflex.returns_management.order.Order;
import com.yottaflex.returns_management.order.OrderLine;
import com.yottaflex.returns_management.returnrequest.Inspection;
import com.yottaflex.returns_management.returnrequest.InspectionRepository;
import com.yottaflex.returns_management.returnrequest.ReturnLine;
import com.yottaflex.returns_management.returnrequest.ReturnLineRepository;
import com.yottaflex.returns_management.returnrequest.ReturnRequest;
import com.yottaflex.returns_management.returnrequest.ReturnRequestRepository;
import com.yottaflex.returns_management.returnrequest.ReturnRequestStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private ReturnRequestRepository returnRequestRepository;

    @Mock
    private ReturnLineRepository returnLineRepository;

    @Mock
    private InspectionRepository inspectionRepository;

    @Mock
    private InventoryDispositionRepository dispositionRepository;

    @InjectMocks
    private RefundService refundService;

    private RefundRequest refundRequest;
    private ReturnRequest returnRequest;

   @BeforeEach
void setUp() {
    refundRequest = new RefundRequest();
    refundRequest.setReturnRequestId(1L);

    returnRequest = mock(ReturnRequest.class);

    lenient().when(returnRequest.getId()).thenReturn(1L);
    lenient().when(returnRequest.getReturnCode()).thenReturn("RET-001");
    lenient().when(returnRequest.getStatus())
            .thenReturn(ReturnRequestStatus.INSPECTED);
}

    @Test
    void createRefund_shouldThrowWhenReturnRequestDoesNotExist() {
        when(returnRequestRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refundService.createRefund(refundRequest)
        );

        assertEquals("Return request not found", exception.getMessage());

        verify(refundRepository, never()).save(any(Refund.class));
    }

    @Test
    void createRefund_shouldThrowWhenRefundAlreadyExists() {
        when(returnRequestRepository.findById(1L))
                .thenReturn(Optional.of(returnRequest));

        when(refundRepository.existsByReturnRequest_Id(1L))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refundService.createRefund(refundRequest)
        );

        assertEquals(
                "A refund already exists for this return request",
                exception.getMessage()
        );

        verify(returnLineRepository, never())
                .findByReturnRequest_Id(any());

        verify(refundRepository, never()).save(any(Refund.class));
    }

    @Test
    void createRefund_shouldThrowWhenReturnIsNotInspected() {
        when(returnRequestRepository.findById(1L))
                .thenReturn(Optional.of(returnRequest));

        when(refundRepository.existsByReturnRequest_Id(1L))
                .thenReturn(false);

        when(returnRequest.getStatus())
                .thenReturn(ReturnRequestStatus.SUBMITTED);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refundService.createRefund(refundRequest)
        );

        assertEquals(
                "Return request must be inspected or refund pending",
                exception.getMessage()
        );

        verify(returnLineRepository, never())
                .findByReturnRequest_Id(any());

        verify(refundRepository, never()).save(any(Refund.class));
    }

    @Test
    void createRefund_shouldThrowWhenReturnHasNoLines() {
        when(returnRequestRepository.findById(1L))
                .thenReturn(Optional.of(returnRequest));

        when(refundRepository.existsByReturnRequest_Id(1L))
                .thenReturn(false);

        when(returnLineRepository.findByReturnRequest_Id(1L))
                .thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refundService.createRefund(refundRequest)
        );

        assertEquals(
                "Cannot refund a return request without return lines",
                exception.getMessage()
        );

        verify(refundRepository, never()).save(any(Refund.class));
    }

    @Test
    void createRefund_shouldThrowWhenInspectionIsMissing() {
        ReturnLine line = mock(ReturnLine.class);
        when(line.getId()).thenReturn(10L);

        when(returnRequestRepository.findById(1L))
                .thenReturn(Optional.of(returnRequest));

        when(refundRepository.existsByReturnRequest_Id(1L))
                .thenReturn(false);

        when(returnLineRepository.findByReturnRequest_Id(1L))
                .thenReturn(List.of(line));

        when(inspectionRepository.findByReturnLine_Id(10L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refundService.createRefund(refundRequest)
        );

        assertEquals(
                "Every return line must be inspected before refund",
                exception.getMessage()
        );

        verify(refundRepository, never()).save(any(Refund.class));
    }

    @Test
    void createRefund_shouldThrowWhenInventoryDispositionIsMissing() {
        ReturnLine line = mock(ReturnLine.class);
        Inspection inspection = mock(Inspection.class);

        when(line.getId()).thenReturn(10L);
        when(inspection.getId()).thenReturn(20L);

        when(returnRequestRepository.findById(1L))
                .thenReturn(Optional.of(returnRequest));

        when(refundRepository.existsByReturnRequest_Id(1L))
                .thenReturn(false);

        when(returnLineRepository.findByReturnRequest_Id(1L))
                .thenReturn(List.of(line));

        when(inspectionRepository.findByReturnLine_Id(10L))
                .thenReturn(Optional.of(inspection));

        when(dispositionRepository.findByInspection_Id(20L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refundService.createRefund(refundRequest)
        );

        assertEquals(
                "Every inspected line must have an inventory disposition",
                exception.getMessage()
        );

        verify(refundRepository, never()).save(any(Refund.class));
    }

    @Test
    void createRefund_shouldCreateRefundSuccessfully() {
        ReturnLine line = mock(ReturnLine.class);
        OrderLine orderLine = mock(OrderLine.class);
        Order order = mock(Order.class);
        Inspection inspection = mock(Inspection.class);
        InventoryDisposition disposition =
                mock(InventoryDisposition.class);

        when(line.getId()).thenReturn(10L);
        when(line.getOrderLine()).thenReturn(orderLine);
        when(line.getQuantityRequested()).thenReturn(2);

        when(orderLine.getUnitPrice()).thenReturn(new BigDecimal("100.00"));
        when(orderLine.getOrder()).thenReturn(order);

        when(order.getId()).thenReturn(50L);
        when(order.getTotalPaid()).thenReturn(new BigDecimal("500.00"));

        when(inspection.getId()).thenReturn(20L);
        when(disposition.getId()).thenReturn(30L);

        when(returnRequestRepository.findById(1L))
                .thenReturn(Optional.of(returnRequest));

        when(refundRepository.existsByReturnRequest_Id(1L))
                .thenReturn(false);

        when(returnLineRepository.findByReturnRequest_Id(1L))
                .thenReturn(List.of(line));

        when(inspectionRepository.findByReturnLine_Id(10L))
                .thenReturn(Optional.of(inspection));

        when(dispositionRepository.findByInspection_Id(20L))
                .thenReturn(Optional.of(disposition));

        when(refundRepository.sumRefundsForOrder(50L))
                .thenReturn(BigDecimal.ZERO);

        when(refundRepository.save(any(Refund.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefundResponse response =
                refundService.createRefund(refundRequest);

        assertNotNull(response);
        assertEquals(1L, response.getReturnRequestId());
        assertEquals("RET-001", response.getReturnCode());
        assertEquals(new BigDecimal("200.00"), response.getAmount());
        assertEquals("PENDING", response.getStatus());
        assertNotNull(response.getCreatedAt());

        verify(refundRepository).save(any(Refund.class));
    }

    @Test
    void createRefund_shouldThrowWhenRefundExceedsAmountPaid() {
        ReturnLine line = mock(ReturnLine.class);
        OrderLine orderLine = mock(OrderLine.class);
        Order order = mock(Order.class);
        Inspection inspection = mock(Inspection.class);
        InventoryDisposition disposition =
                mock(InventoryDisposition.class);

        when(line.getId()).thenReturn(10L);
        when(line.getOrderLine()).thenReturn(orderLine);
        when(line.getQuantityRequested()).thenReturn(2);

        when(orderLine.getUnitPrice()).thenReturn(new BigDecimal("300.00"));
        when(orderLine.getOrder()).thenReturn(order);

        when(order.getId()).thenReturn(50L);
        when(order.getTotalPaid()).thenReturn(new BigDecimal("500.00"));

        when(inspection.getId()).thenReturn(20L);
        when(disposition.getId()).thenReturn(30L);

        when(returnRequestRepository.findById(1L))
                .thenReturn(Optional.of(returnRequest));

        when(refundRepository.existsByReturnRequest_Id(1L))
                .thenReturn(false);

        when(returnLineRepository.findByReturnRequest_Id(1L))
                .thenReturn(List.of(line));

        when(inspectionRepository.findByReturnLine_Id(10L))
                .thenReturn(Optional.of(inspection));

        when(dispositionRepository.findByInspection_Id(20L))
                .thenReturn(Optional.of(disposition));

        when(refundRepository.sumRefundsForOrder(50L))
                .thenReturn(BigDecimal.ZERO);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refundService.createRefund(refundRequest)
        );

        assertTrue(exception.getMessage().contains(
                "Refund exceeds the remaining refundable amount"
        ));

        verify(refundRepository, never()).save(any(Refund.class));
    }
}
