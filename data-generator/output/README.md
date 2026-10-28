# Synthetic Return Management Dataset

All records are fictional and generated locally. No external or personal data is used.

## Reproduce

```powershell
py generate_data.py --scale starter --seed 20261022 --output output
py generate_data.py --scale full --seed 20261022 --output output
```

`starter` produces 3,000 orders, 500 return requests, and 800 return lines. `full` produces 30,000 orders, 5,000 return requests, and 8,000 return lines.

## Files

Each entity is exported as CSV and JSON. `seed_data.sql` contains illustrative INSERT statements. `ground_truth.csv/json` is kept separate from entity input data and identifies deliberate anomalies. `validation_summary.json` reports counts and integrity checks. `source_manifest.json` contains SHA-256 hashes.

## Important modeling note

The dataset deliberately contains five labelled exception scenarios. Some logical constraints are intentionally violated in those records so a downstream validator or decision engine can detect them; primary/foreign keys remain valid. The SQL file is an interchange export and may need table/column adaptation before loading into the application's JPA schema.

## Data dictionary

See `data_dictionary.md` for field names, types, requirements, conventions, and key relationships.
