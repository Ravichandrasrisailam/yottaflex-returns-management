# YottaFlex Returns Console (optional demo UI)

This is a lightweight static UI for the existing Spring Boot REST API. It adds no JavaScript build step and no new backend dependencies.

## Install

Copy the contents of this archive into the root of your existing `returns-management` project, preserving the directory structure. The files are placed at:

- `src/main/resources/static/index.html`
- `src/main/resources/static/styles.css`
- `src/main/resources/static/app.js`
- `docs/database-erd.md`

Your `static` directory was empty when this UI package was prepared, so these files should not overwrite existing UI files.

## Run

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8080/` (or the port configured by your application).

The UI uses same-origin relative API URLs. It does not contain credentials or use mock data.

## Important notes

- The UI is a demonstration interface, not an authentication or production security layer.
- It uses the existing API endpoints and expects the Spring Boot app and PostgreSQL database to be running.
- The “Check API” button uses an order lookup because no health endpoint was confirmed in the controller list.
- Approve/reject uses an editable JSON body because the exact `ReturnDecisionRequest` fields were not included in the supplied controller output. Check `ReturnDecisionRequest.java` before using those actions.
- Use existing test data and unique codes. Creating orders, returns, shipments, dispositions, or refunds writes to the database.
- Some operations depend on earlier IDs and valid lifecycle states. The backend remains the source of truth for validation.
