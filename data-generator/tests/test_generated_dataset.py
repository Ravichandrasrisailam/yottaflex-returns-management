
import csv
import json
import unittest
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[2]
OUTPUT_DIR = PROJECT_ROOT / "data-generator" / "output"

EXPECTED_COUNTS = {
    "orders_row_count": 30000,
    "return_requests_row_count": 5000,
    "return_lines_row_count": 8000,
}

EXPECTED_EDGE_CASES = {
    "RETURN_QUANTITY_EXCEEDS_PURCHASED",
    "REFUND_EXCEEDS_PAID_AMOUNT",
    "RESTOCK_BEFORE_INSPECTION",
    "LATE_RETURN_WITHOUT_OVERRIDE",
    "DUPLICATE_REFUND_ATTEMPT",
}


class GeneratedDatasetTests(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        summary_path = OUTPUT_DIR / "validation_summary.json"
        ground_truth_path = OUTPUT_DIR / "ground_truth.json"

        if not summary_path.exists():
            raise FileNotFoundError(
                f"Missing validation summary: {summary_path}"
            )

        if not ground_truth_path.exists():
            raise FileNotFoundError(
                f"Missing ground truth: {ground_truth_path}"
            )

        cls.summary = json.loads(
            summary_path.read_text(encoding="utf-8")
        )
        cls.ground_truth = json.loads(
            ground_truth_path.read_text(encoding="utf-8")
        )

    def test_required_full_dataset_counts(self):
        for field, expected in EXPECTED_COUNTS.items():
            with self.subTest(field=field):
                self.assertEqual(self.summary[field], expected)

    def test_primary_and_foreign_key_validation_passed(self):
        self.assertEqual(
            self.summary["primary_key_validation"], "PASS"
        )
        self.assertEqual(
            self.summary["foreign_key_validation"], "PASS"
        )
        self.assertEqual(self.summary["validation_errors"], [])

    def test_all_labelled_edge_cases_passed(self):
        self.assertTrue(
            self.summary["all_labelled_edge_cases_verified"]
        )

        validation = self.summary["labelled_edge_case_validation"]
        self.assertEqual(len(validation), 5)

        for case_name, result in validation.items():
            with self.subTest(case=case_name):
                self.assertEqual(result, "PASS")

    def test_ground_truth_contains_five_required_issue_types(self):
        actual_types = {
            case["issue_type"] for case in self.ground_truth
        }

        self.assertEqual(len(self.ground_truth), 5)
        self.assertEqual(actual_types, EXPECTED_EDGE_CASES)

    def test_ground_truth_is_separate_from_entity_input(self):
        ground_truth_csv = OUTPUT_DIR / "ground_truth.csv"
        self.assertTrue(ground_truth_csv.exists())

        with ground_truth_csv.open(
            "r", newline="", encoding="utf-8"
        ) as file:
            rows = list(csv.DictReader(file))

        self.assertEqual(len(rows), 5)
        self.assertTrue(
            all(row.get("case_id") for row in rows)
        )


if __name__ == "__main__":
    unittest.main(verbosity=2)
