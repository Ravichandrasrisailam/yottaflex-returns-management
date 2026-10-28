# Data Dictionary

All timestamps use ISO 8601 local timestamps; currency is USD. Amounts are decimal strings with two fractional digits. IDs are deterministic integers; business codes are zero-padded and immutable.

## customers

customer_id: integer PK, required; customer_code: unique CUS-######; full_name/email: required synthetic values; created_at: required timestamp.

## orders

order_id: integer PK; order_code: unique ORD-2026-######; customer_id: FK customers; ordered_at required; delivered_at optional and after ordered_at; status DELIVERED/PROCESSING; subtotal, discount_amount, tax_amount, shipping_fee, total_paid decimal USD; currency USD.

## order_lines

order_line_id PK; order_id FK; line_number positive per order; sku required; category from ELECTRONICS/HOME/APPAREL/BOOKS/ACCESSORIES; quantity positive integer; unit_price/discount_amount/line_total decimal USD.

## return_requests

return_request_id PK; return_code unique RMA-2026-######; order_id/customer_id FKs; requested_at timestamp; customer_reason from REASONS; status from STATUSES; policy_window_days positive; override_approved/is_exchange booleans.

## return_lines

return_line_id PK; return_line_code RMAL-######-##; return_request_id/order_line_id FKs; quantity_requested positive integer; one order line per row.

## return_authorizations

authorization_id PK; authorization_code unique; return_request_id FK; decision APPROVED/REJECTED/PENDING; decided_at optional; decision_reason required; override_approved boolean.

## return_shipments

shipment_id PK; shipment_code unique RTS-2026-######; return_request_id FK; carrier/tracking_number required; shipped_at required; delivered_at optional; status LABEL_CREATED/IN_TRANSIT/DELIVERED/LOST/CANCELLED.

## inspections

inspection_id PK; return_line_id FK; condition LIKE_NEW/GOOD/DAMAGED/DEFECTIVE/INCOMPLETE; inspected_at required; notes required.

## refunds

refund_id PK; refund_code unique REF-2026-######; return_request_id FK; amount decimal USD; status PENDING/COMPLETED/FAILED/DUPLICATE_ATTEMPT; created_at required; processed_at optional. A deliberate duplicate attempt is labelled in ground truth.

## inventory_dispositions

disposition_id PK; inspection_id and return_line_id FKs; disposition_type RESTOCK/REFURBISH/QUARANTINE/SCRAP; decision_reason required; decided_at required and normally not before inspection. EDGE-003 violates timing intentionally.

## Relationships and cardinality

- Customer 1:N Orders.
- Order 1:N OrderLines.
- Order 1:N ReturnRequests; each ReturnRequest belongs to one Customer and one Order.
- ReturnRequest 1:N ReturnLines and 1:1 ReturnAuthorization in this dataset.
- ReturnRequest 0:1 ReturnShipment and 0:N Refund records (duplicate-attempt edge case can create more than one).
- ReturnLine 0:1 Inspection.
- Inspection 0:1 InventoryDisposition.
- Effective return policy: 30 calendar days from delivery; late requests without override are rejected.
