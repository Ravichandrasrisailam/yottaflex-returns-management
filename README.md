\# YottaFlex AI — Returns Management System



A return-management backend built with Java, Spring Boot, PostgreSQL, and a deterministic Python synthetic-data generator.



\## Project Overview



This project models the return lifecycle, including customer and order records, return requests and lines, eligibility decisions, return shipments, inspections, inventory dispositions, and refunds.



It also includes a synthetic dataset generator for validating return-management workflows and identifying edge cases.



\## Technology Stack



\- Java 21

\- Spring Boot

\- Spring Data JPA / Hibernate

\- PostgreSQL

\- Python standard library

\- Maven

\- CSV, JSON, and SQL exports



\## Main Features



\- Customer and order management

\- Return-request creation and return-line validation

\- Return eligibility and approval decisions

\- Return shipment status transitions

\- Inspection and inventory-disposition recording

\- Refund creation with duplicate and cumulative payment-limit checks

\- Synthetic data generation with deterministic seeds

\- Primary-key, foreign-key, and labelled edge-case validation



\## Synthetic Dataset



The generator is located at `data-generator/generate\_data.py`.



All records are fictional. No external or personal data is used.



| Dataset | Starter | Full |

|---|---:|---:|

| Orders | 3,000 | 30,000 |

| Return requests | 500 | 5,000 |

| Return lines | 800 | 8,000 |



The full run also generates customers, order lines, authorizations, shipments, inspections, refunds, and inventory dispositions.



\### Generate the dataset



Run these commands from the repository root:



```powershell

py .\\data-generator\\generate\_data.py --scale starter --seed 20261022 --output .\\data-generator\\output

```



For the full dataset:



```powershell

py .\\data-generator\\generate\_data.py --scale full --seed 20261022 --output .\\data-generator\\output

```



The fixed seed makes generation reproducible. Generated files are written to `data-generator/output/`.



\### Generated files



\- Entity-level CSV and JSON exports

\- `ground\_truth.csv` and `ground\_truth.json` — labelled edge cases, separate from entity input data

\- `validation\_summary.json` — record counts and validation results

\- `data\_dictionary.md` — field descriptions and relationships

\- `source\_manifest.json` — SHA-256 file hashes

\- `seed\_data.sql` — illustrative SQL seed statements



\### Labelled edge cases



The generator includes five deliberate exception scenarios:



1\. Return quantity exceeds purchased quantity

2\. Refund exceeds the amount paid

3\. Restocking occurs before inspection

4\. Late return without an approved override

5\. Duplicate refund attempt



These cases are identified separately in ground truth so a downstream validator or decision engine can evaluate them.



\## Running the Spring Boot Application



\### Prerequisites



\- Java 21

\- PostgreSQL

\- Windows PowerShell or a compatible terminal



Create a PostgreSQL database named `yottaflex\_returns`, then configure the database credentials through environment variables. Do not commit passwords or other secrets.



In PowerShell:



```powershell

$env:DB\_USERNAME = "postgres"

$env:DB\_PASSWORD = Read-Host "Enter your PostgreSQL password"

```



Start the application from the repository root:



```powershell

.\\mvnw.cmd spring-boot:run

```



The API runs on `http://localhost:8080` by default.



Run tests with:



```powershell

.\\mvnw.cmd clean test

```



\## Data and Schema Notes



The synthetic dataset is a separate deliverable from the live JPA application. The generated `seed\_data.sql` is an interchange export and may require table or column adaptation before it can be loaded into the application's database schema.



The dataset intentionally includes logical anomalies in the labelled records. Primary-key and foreign-key integrity checks are separate from business-rule checks.



\## Validation and Reproducibility



The full dataset was generated with seed `20261022`. The validation summary reported the expected full-scale counts, successful primary-key and foreign-key checks, all five labelled edge cases passing their checks, and no reported validation errors.



A repeat run using the same seed was compared using SHA-256 hashes. The compared output files were identical.



\## Project Structure



```text

returns-management/

├── src/

│   └── main/

│       ├── java/

│       └── resources/

├── data-generator/

│   ├── generate\_data.py

│   ├── tests/

│   └── output/

├── pom.xml

├── mvnw

├── mvnw.cmd

└── README.md

```



\## Limitations and Future Improvements



\- Add broader automated tests for business rules and API lifecycle transitions.

\- Add automated tests for synthetic-data generation and reproducibility.

\- Add concurrency protection for refunds to prevent simultaneous requests from exceeding payment limits.

\- Provide a tested SQL import path compatible with the application's JPA schema.

\- Add database migrations and API documentation.



\## Responsible AI Use



AI tools were used as development assistance. The implementation, validation, and final submission should be reviewed and understood by the project author. Development prompts and AI-assisted changes should be documented in accordance with the assignment requirements.



