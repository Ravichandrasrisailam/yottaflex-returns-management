# Database ERD — YottaFlex Returns Management

The diagram below is based on the PostgreSQL schema and foreign-key/unique-constraint output shared during development.

```mermaid
erDiagram
    CUSTOMERS ||--o{ CUSTOMER_ORDERS : places
    CUSTOMER_ORDERS ||--o{ ORDER_LINES : contains
    CUSTOMER_ORDERS ||--o{ RETURN_REQUESTS : has
    RETURN_REQUESTS ||--o{ RETURN_LINES : contains
    ORDER_LINES ||--o{ RETURN_LINES : references
    RETURN_LINES ||--o| RETURN_INSPECTIONS : has
    RETURN_INSPECTIONS ||--o| INVENTORY_DISPOSITIONS : has
    RETURN_REQUESTS ||--o{ RETURN_SHIPMENTS : has
    RETURN_REQUESTS ||--o| REFUNDS : has

    CUSTOMERS {
        bigint id PK
        varchar customer_code UK
        varchar email UK
        varchar full_name
        timestamp created_at
    }
    CUSTOMER_ORDERS {
        bigint id PK
        bigint customer_id FK
        varchar order_code UK
        timestamp ordered_at
        timestamp delivered_at
        varchar status
        numeric total_paid
    }
    ORDER_LINES {
        bigint id PK
        bigint order_id FK
        integer line_number
        varchar sku
        integer quantity
        numeric unit_price
    }
    RETURN_REQUESTS {
        bigint id PK
        bigint order_id FK
        varchar return_code UK
        varchar reason
        timestamp requested_at
        varchar status
    }
    RETURN_LINES {
        bigint id PK
        bigint return_request_id FK
        bigint order_line_id FK
        integer quantity_requested
    }
    RETURN_INSPECTIONS {
        bigint id PK
        bigint return_line_id FK_UK
        varchar condition_found
        timestamp inspected_at
        varchar notes
    }
    INVENTORY_DISPOSITIONS {
        bigint id PK
        bigint inspection_id FK_UK
        varchar disposition_type
        varchar decision_reason
        timestamp decided_at
    }
    RETURN_SHIPMENTS {
        bigint id PK
        bigint return_request_id FK
        varchar shipment_code UK
        varchar carrier
        varchar tracking_number
        varchar status
        timestamp shipped_at
        timestamp delivered_at
    }
    REFUNDS {
        bigint id PK
        bigint return_request_id FK_UK
        numeric amount
        timestamp created_at
        varchar status
    }
```

`PK` = primary key; `FK` = foreign key; `UK` = unique constraint. Data types are shortened for readability.
